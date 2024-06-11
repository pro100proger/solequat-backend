package com.solequat.businesslogic.service.implementation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.CalculationDataIdDTO;
import com.core.dto.IntermediateResultDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.PaymentDTO;
import com.core.entity.Eigenvalues;
import com.core.entity.Equation;
import com.core.entity.EigenvaluesRequest;
import com.core.entity.LinearSystemRequest;
import com.core.entity.User;
import com.core.repository.EigenvaluesRepository;
import com.core.repository.EigenvaluesRequestRepository;
import com.core.repository.EquationRepository;
import com.core.repository.LinearSystemRepository;
import com.core.repository.UserRepository;
import com.solequat.businesslogic.service.WorkerClientService;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkerClientServiceImpl implements WorkerClientService {

    private final EquationRepository equationRepository;
    private final EigenvaluesRepository eigenvaluesRepository;
    private final UserRepository userRepository;
    private final LinearSystemRepository linearSystemRepository;
    private final EigenvaluesRequestRepository eigenvaluesRequestRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public WorkerClientServiceImpl(EquationRepository equationRepository, EigenvaluesRepository eigenvaluesRepository, UserRepository userRepository,
        LinearSystemRepository linearSystemRepository, EigenvaluesRequestRepository eigenvaluesRequestRepository, RestTemplate restTemplate) {
        this.equationRepository = equationRepository;
        this.eigenvaluesRepository = eigenvaluesRepository;
        this.userRepository = userRepository;
        this.linearSystemRepository = linearSystemRepository;
        this.eigenvaluesRequestRepository = eigenvaluesRequestRepository;
        this.restTemplate = restTemplate;
    }

    @Value("${routes.uris.route1}")
    private String uri1;
    @Value("${routes.uris.route2}")
    private String uri2;
    @Value("${routes.uris.route3}")
    private String uri3;
    @Value("${routes.uris.route4}")
    private String uri4;
    @Value("${routes.uris.route5}")
    private String uri5;
    @Value("${routes.uris.route6}")
    private String uri6;
    @Value("${routes.uris.route7}")
    private String uri7;
    @Value("${routes.uris.route8}")
    private String uri8;
    @Value("${routes.uris.route9}")
    private String uri9;
    @Value("${routes.uris.route10}")
    private String uri10;
    @Value("${routes.uris.route11}")
    private String uri11;
    @Value("${routes.uris.route12}")
    private String uri12;


    public IntermediateResultDTO calculateEquationFirstStage(MultipartFile matrixFile, MultipartFile vectorFile, String userId)
        throws IOException {
        log.info("WorkerClientService: Calculate request to worker");

        LocalDateTime startCalculation = LocalDateTime.now();

        Equation equation = new Equation(startCalculation);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        equation.setUser(user);
        equationRepository.save(equation);

        LinearSystemRequest linearSystemRequest = new LinearSystemRequest();
        byte[] matrixBytes = matrixFile.getBytes();
        String fileContent = new String(matrixBytes);

        double[][] matrixArray = Arrays.stream(fileContent.trim().split("\n"))
            .map(line -> Arrays.stream(line.trim().split(";"))
                .mapToDouble(Double::parseDouble)
                .toArray())
            .toArray(double[][]::new);

        log.info(Arrays.deepToString(matrixArray));

        linearSystemRequest.setMatrix(matrixArray);

        byte[] vectorBytes = vectorFile.getBytes();
        String fileContent2 = new String(vectorBytes);

        double[] vectorArray = Arrays.stream(fileContent2.trim().split("\n"))
            .flatMap(line -> Arrays.stream(line.trim().split("\\s+")))
            .filter(s -> !s.isEmpty())
            .mapToDouble(Double::parseDouble)
            .toArray();

        log.info(Arrays.toString(vectorArray));

        linearSystemRequest.setVector(vectorArray);
        linearSystemRepository.save(linearSystemRequest);

        CalculationDataIdDTO equationIdDTO = new CalculationDataIdDTO();
        equationIdDTO.setPostgresId(equation.getId());
        equationIdDTO.setMongoDBId(linearSystemRequest.getId());



        log.info("WorkerClientService: restTemplate request to worker");
        IntermediateResultDTO intermediateResultDTO = restTemplate.postForObject(
            uri1,
            equationIdDTO,
            IntermediateResultDTO.class);

        log.info("WorkerClientService: Response from worker");
        return intermediateResultDTO;
    }

    public CalculationDataResultDTO getEquationById(String id) {
        log.info("WorkerClientService: Get equation by id request to worker");

        return restTemplate.getForObject(
            uri2 + id,
            CalculationDataResultDTO.class);
    }

    public List<CalculationDataHistoryDTO> getAllEquationsByUserId(String userId) {
        log.info("WorkerClientService: Get all equations by user id {} request to worker", userId);
        ResponseEntity<List<CalculationDataHistoryDTO>> response = restTemplate
            .exchange(
                uri3 + userId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CalculationDataHistoryDTO>>() {}
        );

        return response.getBody();
    }

    public byte[] getResultById(String id) {
        log.info("WorkerClientService: Get result by id. ");

        return restTemplate.getForObject(
            uri4 + id,
                byte[].class);
    }

    public byte[] getVectorById(String id) {
        log.info("WorkerClientService: Get vector by id. ");

        return restTemplate.getForObject(
            uri5 + id,
            byte[].class);
    }

    public byte[] getMatrixById(String id) {
        log.info("WorkerClientService: Get matrix by id: {}", id);

        return restTemplate.getForObject(
            uri6 + id,
            byte[].class);
    }


    public PaymentDTO getAllCalculationsByUserIdAndIsPaid(String userId) {
        log.info("WorkerClientService: Get all calculations by user id {} request to worker", userId);
        ResponseEntity<PaymentDTO> response = restTemplate
            .exchange(
                uri7 + userId,
                HttpMethod.GET,
                null,
                PaymentDTO.class
            );

        return response.getBody();
    }

    public IntermediateResultDTO calculateEigenvaluesFirstStage(MultipartFile matrixFile, String userId)
        throws IOException {
        log.info("WorkerClientService: Calculate eigenvalues request to worker");

        LocalDateTime startCalculation = LocalDateTime.now();


        Eigenvalues eigenvalues = new Eigenvalues(startCalculation);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        eigenvalues.setUser(user);
        eigenvaluesRepository.save(eigenvalues);

        EigenvaluesRequest eigenvaluesRequest = new EigenvaluesRequest();
        byte[] matrixBytes = matrixFile.getBytes();
        String fileContent = new String(matrixBytes);

        double[][] matrixArray = Arrays.stream(fileContent.trim().split("\n"))
            .map(line -> Arrays.stream(line.trim().split(";"))
                .mapToDouble(Double::parseDouble)
                .toArray())
            .toArray(double[][]::new);

        log.info(Arrays.deepToString(matrixArray));

        eigenvaluesRequest.setMatrix(matrixArray);


        eigenvaluesRequestRepository.save(eigenvaluesRequest);

        CalculationDataIdDTO eigenvaluesIdDTO = new CalculationDataIdDTO();
        eigenvaluesIdDTO.setPostgresId(eigenvalues.getId());
        eigenvaluesIdDTO.setMongoDBId(eigenvaluesRequest.getId());


        log.info("WorkerClientService: restTemplate eigenvalues request to worker");
        IntermediateResultDTO intermediateResultDTO = restTemplate.postForObject(
            uri8,
            eigenvaluesIdDTO,
            IntermediateResultDTO.class);

        log.info("WorkerClientService: Response from worker");
        return intermediateResultDTO;
    }


    public CalculationDataResultDTO getEigenvaluesById(String id) {
        log.info("WorkerClientService: Get eigenvalues by id request to worker");

        return restTemplate.getForObject(
            uri9 + id,
            CalculationDataResultDTO.class);
    }

    public List<CalculationDataHistoryDTO> getAllEigenvaluesByUserId(String userId) {
        log.info("WorkerClientService: Get all eigenvalues by user id {} request to worker", userId);
        ResponseEntity<List<CalculationDataHistoryDTO>> response = restTemplate
            .exchange(
                uri10 + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CalculationDataHistoryDTO>>() {}
            );

        return response.getBody();
    }

    public byte[] getEigenvaluesResultById(String id) {
        log.info("WorkerClientService: Get eigenvalues result by id. ");

        return restTemplate.getForObject(
            uri11 + id,
            byte[].class);
    }

    public byte[] getEigenvaluesMatrixById(String id) {
        log.info("WorkerClientService: Get eigenvalues matrix by id: {}", id);

        return restTemplate.getForObject(
            uri12 + id,
            byte[].class);
    }
}
