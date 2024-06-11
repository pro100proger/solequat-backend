package com.solequat.worker.service.implementation;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.CalculationDataIdDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.IntermediateResultDTO;
import com.core.entity.Eigenvalues;
import com.core.entity.EigenvaluesRequest;
import com.core.entity.EigenvaluesResult;
import com.core.entity.Equation;
import com.core.entity.LinearSystemRequest;
import com.core.entity.LinearSystemResult;
import com.core.repository.EigenvaluesRepository;
import com.core.repository.EigenvaluesRequestRepository;
import com.core.repository.EigenvaluesResultRepository;
import com.solequat.worker.service.EigenvaluesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EigenvaluesServiceImpl implements EigenvaluesService {

    private final EigenvaluesRepository eigenvaluesRepository;
    private final EigenvaluesRequestRepository eigenvaluesRequestRepository;
    private final EigenvaluesResultRepository eigenvaluesResultRepository;

    public EigenvaluesServiceImpl(EigenvaluesRepository eigenvaluesRepository, EigenvaluesRequestRepository eigenvaluesRequestRepository,
        EigenvaluesResultRepository eigenvaluesResultRepository) {
        this.eigenvaluesRepository = eigenvaluesRepository;
        this.eigenvaluesRequestRepository = eigenvaluesRequestRepository;
        this.eigenvaluesResultRepository = eigenvaluesResultRepository;
    }

    public IntermediateResultDTO calculateEigenvaluesFirstStage(CalculationDataIdDTO eigenvaluesIdDTO) throws Exception {
        log.info("EigenvaluesServiceImpl: calculate eigenvalues first stage");

        Optional<Eigenvalues> eigenvaluesOptional = eigenvaluesRepository.findById(eigenvaluesIdDTO.getPostgresId());
        Eigenvalues eigenvalues;
        if (eigenvaluesOptional.isPresent()) {
            eigenvalues = eigenvaluesOptional.get();
        } else {
            throw new Exception("Error: eigenvalues is not present");
        }

        Optional<EigenvaluesRequest> eigenvaluesRequestOptional =
            eigenvaluesRequestRepository.findById(eigenvaluesIdDTO.getMongoDBId());
        EigenvaluesRequest eigenvaluesRequest;
        if (eigenvaluesRequestOptional.isPresent()) {
            eigenvaluesRequest = eigenvaluesRequestOptional.get();
        } else {
            throw new Exception("Error: eigenvaluesRequest is not present");
        }

        IntermediateResultDTO firstStageResult = new IntermediateResultDTO();
        firstStageResult.setStartCalculation(eigenvalues.getStartCalculation());


        new Thread(() -> {
            try {
                calculateEigenvaluesSecondStage(eigenvalues, eigenvaluesRequest);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        firstStageResult.setId(eigenvalues.getId());
        return firstStageResult;
    }

    public void calculateEigenvaluesSecondStage(Eigenvalues eigenvalues, EigenvaluesRequest eigenvaluesRequest)
        throws InterruptedException {
        log.info("EigenvaluesServiceImpl: calculate eigenvalues second stage");
        long start = System.currentTimeMillis();

        CalculationDataResultDTO secondStageResult = new CalculationDataResultDTO();

        SimpleMatrix matrix = new SimpleMatrix(eigenvaluesRequest.getMatrix());

        SimpleEVD<SimpleMatrix> evd = matrix.eig();

        double[] result = new double[evd.getNumberOfEigenvalues()];
        for (int i = 0; i < evd.getNumberOfEigenvalues(); i++) {
            result[i] = evd.getEigenvalue(i).getReal();
        }

        log.info("Eigenvalues result: " + Arrays.toString(result));


        EigenvaluesResult eigenvaluesResult = new EigenvaluesResult();
        eigenvaluesResult.setResultVector(result);

        eigenvaluesResultRepository.save(eigenvaluesResult);

        Thread.sleep(10);
        long finish = System.currentTimeMillis();
        long duration = 0;
        duration = finish - start;

        secondStageResult.setDuration(duration);
        eigenvalues.setDuration(duration);

        LocalDateTime endCalculation =  LocalDateTime.now().plusSeconds(duration);

        secondStageResult.setEndCalculation(endCalculation);
        eigenvalues.setEndCalculation(endCalculation);
        eigenvalues.setDataId(eigenvaluesRequest.getId());
        eigenvalues.setResultId(eigenvaluesResult.getId());

        eigenvaluesRepository.save(eigenvalues);
        log.info("EigenvaluesServiceImpl: end eigenvalues calculation");
    }


    public CalculationDataResultDTO getEigenvaluesById(String id) {
        Eigenvalues eigenvalues = eigenvaluesRepository.getEigenvaluesById(id);
        log.info("EigenvaluesServiceImpl: Get eigenvalues by id in service");

        if (!eigenvaluesRepository.existsById(id)) {
            log.error(String.format("EigenvaluesServiceImpl: Eigenvalues not found by id: %s", id));
            throw new RuntimeException();
        }
        CalculationDataResultDTO eigenvaluesResultDTO = new CalculationDataResultDTO();
        eigenvaluesResultDTO.setDuration(eigenvalues.getDuration());
        eigenvaluesResultDTO.setEndCalculation(eigenvalues.getEndCalculation());
        eigenvaluesResultDTO.setStartCalculation(eigenvalues.getStartCalculation());
        eigenvaluesResultDTO.setDataId(eigenvalues.getDataId());
        eigenvaluesResultDTO.setResultId(eigenvalues.getResultId());

        return eigenvaluesResultDTO;
    }


    public List<CalculationDataHistoryDTO> getAllEigenvaluesByUserId(String userId) {
        log.info("EigenvaluesServiceImpl: Get all equations by user id {} ", userId);
        List<Eigenvalues> eigenvalues = eigenvaluesRepository.getAllEigenvaluesByUserId(userId)
            .orElseThrow(EntityNotFoundException::new);

        return eigenvalues.stream()
            .map(eigenvalue -> {
                CalculationDataHistoryDTO eigenvaluesHistoryDTO = new CalculationDataHistoryDTO();
                eigenvaluesHistoryDTO.setId(eigenvalue.getId());
                eigenvaluesHistoryDTO.setStartCalculation(eigenvalue.getStartCalculation());
                eigenvaluesHistoryDTO.setEndCalculation(eigenvalue.getEndCalculation());
                eigenvaluesHistoryDTO.setDuration(eigenvalue.getDuration());
                return eigenvaluesHistoryDTO;
            })
            .collect(Collectors.toList());
    }

    public byte[] getEigenvaluesResultById(String id) throws Exception {
        log.info("EigenvaluesServiceImpl: Get result by id. ");
        Optional<EigenvaluesResult> eigenvaluesResultOptional = eigenvaluesResultRepository.findById(id);
        EigenvaluesResult eigenvaluesResult;

        if (eigenvaluesResultOptional.isPresent()) {
            eigenvaluesResult = eigenvaluesResultOptional.get();
        } else {
            throw new Exception("Error: eigenvaluesResult is not present");
        }

        StringBuilder csvData = new StringBuilder();
        for (double value : eigenvaluesResult.getResultVector()) {
            csvData.append(value).append("\n");
        }

        return csvData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getEigenvaluesMatrixById(String id) throws Exception {
        log.info("EigenvaluesServiceImpl: Get matrix by id: {}", id);

        Optional<EigenvaluesRequest> eigenvaluesRequestOptional = eigenvaluesRequestRepository.findById(id);
        if (eigenvaluesRequestOptional.isPresent()) {
            EigenvaluesRequest eigenvaluesRequest = eigenvaluesRequestOptional.get();

            double[][] matrix = eigenvaluesRequest.getMatrix();

            StringBuilder csvContent = new StringBuilder();
            for (double[] row : matrix) {
                for (double value : row) {
                    csvContent.append(value).append(";");
                }
                csvContent.setLength(csvContent.length() - 1);
                csvContent.append("\n");
            }
            return csvContent.toString().getBytes(StandardCharsets.UTF_8);
        } else {
            throw new Exception("Error: eigenvaluesRequest is not present");
        }
    }
}
