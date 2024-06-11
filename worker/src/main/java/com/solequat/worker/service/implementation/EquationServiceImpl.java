package com.solequat.worker.service.implementation;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.CalculationDataIdDTO;
import com.core.dto.IntermediateResultDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.PaymentDTO;
import com.core.entity.Eigenvalues;
import com.core.entity.Equation;
import com.core.entity.LinearSystemRequest;
import com.core.entity.LinearSystemResult;
import com.core.repository.EigenvaluesRepository;
import com.core.repository.EquationRepository;
import com.core.repository.LinearSystemRepository;
import com.core.repository.LinearSystemResultRepository;
import com.solequat.worker.service.EquationService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class EquationServiceImpl implements EquationService {

    private final EquationRepository equationRepository;
    private final EigenvaluesRepository eigenvaluesRepository;
    private final LinearSystemResultRepository linearSystemResultRepository;
    private final LinearSystemRepository linearSystemRepository;

    @Autowired
    public EquationServiceImpl(EquationRepository equationRepository,
        EigenvaluesRepository eigenvaluesRepository, LinearSystemResultRepository linearSystemResultRepository, LinearSystemRepository linearSystemRepository) {
        this.equationRepository = equationRepository;
        this.eigenvaluesRepository = eigenvaluesRepository;
        this.linearSystemResultRepository = linearSystemResultRepository;
        this.linearSystemRepository = linearSystemRepository;
    }

    public IntermediateResultDTO calculateEquationFirstStage(CalculationDataIdDTO equationIdDTO) throws Exception {
        log.info("EquationService: calculate equation first stage");

        Optional<Equation> equationOptional = equationRepository.findById(equationIdDTO.getPostgresId());
        Equation equation;
        if (equationOptional.isPresent()) {
            equation = equationOptional.get();
        } else {
            throw new Exception("Error: equation is not present");
        }

        Optional<LinearSystemRequest> linearSystemRequestOptional = linearSystemRepository.findById(equationIdDTO.getMongoDBId());
        LinearSystemRequest linearSystemRequest;
        if (linearSystemRequestOptional.isPresent()) {
            linearSystemRequest = linearSystemRequestOptional.get();
        } else {
            throw new Exception("Error: linearSystemRequest is not present");
        }

        IntermediateResultDTO firstStageResult = new IntermediateResultDTO();
        firstStageResult.setStartCalculation(equation.getStartCalculation());


        new Thread(() -> {
            try {
                calculateEquationSecondStage(equation, linearSystemRequest);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        firstStageResult.setId(equation.getId());
        return firstStageResult;
    }

    public void calculateEquationSecondStage(Equation equation, LinearSystemRequest linearSystemRequest) throws InterruptedException {
        log.info("EquationService: calculate equation second stage");
        long start = System.currentTimeMillis();

        CalculationDataResultDTO secondStageResult = new CalculationDataResultDTO();

        DMatrixRMaj matrix = new DMatrixRMaj(linearSystemRequest.getMatrix());
        DMatrixRMaj vector = new DMatrixRMaj(linearSystemRequest.getVector());

        DMatrixRMaj result = new DMatrixRMaj();
        CommonOps_DDRM.solve(matrix, vector, result);

        double[] resultArray = result.data;

        LinearSystemResult linearSystemResult = new LinearSystemResult();
        linearSystemResult.setResultVector(resultArray);

        linearSystemResultRepository.save(linearSystemResult);

        Thread.sleep(10);
        long finish = System.currentTimeMillis();
        long duration = 0;
        duration = finish - start;

        secondStageResult.setDuration(duration);
        equation.setDuration(duration);

        LocalDateTime endCalculation =  LocalDateTime.now().plusSeconds(duration);

        secondStageResult.setEndCalculation(endCalculation);
        equation.setEndCalculation(endCalculation);
        equation.setDataId(linearSystemRequest.getId());
        equation.setResultId(linearSystemResult.getId());

        equationRepository.save(equation);
        log.info("EquationService: end calculation");
    }


    public CalculationDataResultDTO getEquationById(String id) {
        Equation equation = equationRepository.getEquationById(id);
        log.info("EquationService: Get equation by id in service");
        if (!equationRepository.existsById(id)) {
            log.error(String.format("EquationService: Equation not found by id: %s", id));
            throw new RuntimeException();
        }
        CalculationDataResultDTO equationResultDTO = new CalculationDataResultDTO();
        equationResultDTO.setDuration(equation.getDuration());
        equationResultDTO.setEndCalculation(equation.getEndCalculation());
        equationResultDTO.setStartCalculation(equation.getStartCalculation());
        equationResultDTO.setDataId(equation.getDataId());
        equationResultDTO.setResultId(equation.getResultId());

        return equationResultDTO;
    }


    public List<CalculationDataHistoryDTO> getAllEquationsByUserId(String userId) {
        log.info("EquationService: Get all equations by user id {} ", userId);
        List<Equation> equations = equationRepository.getAllEquationsByUserId(userId).orElseThrow(EntityNotFoundException::new);

        return equations.stream()
            .map(equation -> {
                CalculationDataHistoryDTO equationHistoryDTO = new CalculationDataHistoryDTO();
                equationHistoryDTO.setId(equation.getId());
                equationHistoryDTO.setStartCalculation(equation.getStartCalculation());
                equationHistoryDTO.setEndCalculation(equation.getEndCalculation());
                equationHistoryDTO.setDuration(equation.getDuration());
                return equationHistoryDTO;
            })
            .collect(Collectors.toList());
    }

    public byte[] getResultById(String id) throws Exception {
        log.info("EquationService: Get result by id. ");
        Optional<LinearSystemResult> linearSystemResultOptional = linearSystemResultRepository.findById(id);
        LinearSystemResult linearSystemResult;
        if (linearSystemResultOptional.isPresent()) {
            linearSystemResult = linearSystemResultOptional.get();
        } else {
            throw new Exception("Error: linearSystemResult is not present");
        }

        StringBuilder csvData = new StringBuilder();
        for (double value : linearSystemResult.getResultVector()) {
            csvData.append(value).append("\n");
        }

        return csvData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getVectorById(String id) throws Exception {
        log.info("EquationService: Get vector by id. ");
        Optional<LinearSystemRequest> linearSystemRequestOptional = linearSystemRepository.findById(id);
        LinearSystemRequest linearSystemRequest;
        if (linearSystemRequestOptional.isPresent()) {
            linearSystemRequest = linearSystemRequestOptional.get();
        } else {
            throw new Exception("Error: linearSystemResult is not present");
        }

        StringBuilder csvData = new StringBuilder();
        for (double value : linearSystemRequest.getVector()) {
            csvData.append(value).append("\n");
        }

        return csvData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getMatrixById(String id) throws Exception {
        log.info("EquationService: Get matrix by id: {}", id);

        Optional<LinearSystemRequest> linearSystemRequestOptional = linearSystemRepository.findById(id);
        if (linearSystemRequestOptional.isPresent()) {
            LinearSystemRequest linearSystemRequest = linearSystemRequestOptional.get();

            double[][] matrix = linearSystemRequest.getMatrix();

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
            throw new Exception("Error: linearSystemRequest is not present");
        }
    }

    public PaymentDTO getAllEquationsByUserIdAndIsPaid(String userId) {
        log.info("EquationService: Get all equations by user id and isPaid {} ", userId);
        List<Equation> equations = equationRepository.getAllEquationsByUserIdAndIsPaid(userId, false)
            .orElseThrow(EntityNotFoundException::new);
        List<Eigenvalues> eigenvalues = eigenvaluesRepository.getAllEigenvaluesByUserIdAndIsPaid(userId, false)
            .orElseThrow(EntityNotFoundException::new);

        long totalCalculations = 0;
        double totalDuration = 0;
        double priceCoefficient = -0.01;


        for (Equation equation : equations) {
            totalDuration += equation.getDuration();
            totalCalculations += 1;
        }
        for (Eigenvalues eigenvalue : eigenvalues) {
            totalDuration += eigenvalue.getDuration();
            totalCalculations += 1;
        }

        totalDuration = totalDuration / 1000;
        double totalPrice = totalDuration * priceCoefficient;

        return PaymentDTO.builder()
            .totalDuration(totalDuration)
            .totalCalculations(totalCalculations)
            .totalPrice(totalPrice)
            .build();
    }
}
