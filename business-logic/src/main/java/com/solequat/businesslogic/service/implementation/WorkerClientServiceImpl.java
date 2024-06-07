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

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIdDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;
import com.core.dto.PaymentDTO;
import com.core.entity.Equation;
import com.core.entity.LinearSystemRequest;
import com.core.entity.User;
import com.core.repository.EquationRepository;
import com.core.repository.LinearSystemRepository;
import com.core.repository.UserRepository;
import com.solequat.businesslogic.service.WorkerClientService;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkerClientServiceImpl implements WorkerClientService {

    private final EquationRepository equationRepository;
    private final UserRepository userRepository;
    private final LinearSystemRepository linearSystemRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public WorkerClientServiceImpl(EquationRepository equationRepository, UserRepository userRepository,
        LinearSystemRepository linearSystemRepository, RestTemplate restTemplate) {
        this.equationRepository = equationRepository;
        this.userRepository = userRepository;
        this.linearSystemRepository = linearSystemRepository;
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


    public EquationIntermediateResultDTO calculateEquationFirstStage(MultipartFile matrixFile, MultipartFile vectorFile, String userId)
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

        EquationIdDTO equationIdDTO = new EquationIdDTO();
        equationIdDTO.setEquationId(equation.getId());
        equationIdDTO.setLinearSystemId(linearSystemRequest.getId());



        log.info("WorkerClientService: restTemplate request to worker");
        EquationIntermediateResultDTO equationIntermediateResultDTO = restTemplate.postForObject(
            uri1,
            equationIdDTO,
            EquationIntermediateResultDTO.class);

        log.info("WorkerClientService: Response from worker");
        return equationIntermediateResultDTO;
    }

    public EquationResultDTO getEquationById(String id) {
        log.info("WorkerClientService: Get equation by id request to worker");

        return restTemplate.getForObject(
            uri2 + id,
            EquationResultDTO.class);
    }

    public List<EquationHistoryDTO> getAllEquationsByUserId(String userId) {
        log.info("WorkerClientService: Get all equations by user id {} request to worker", userId);
        ResponseEntity<List<EquationHistoryDTO>> response = restTemplate
            .exchange(
                uri3 + userId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<EquationHistoryDTO>>() {}
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


    public PaymentDTO getAllEquationsByUserIdAndIsPaid(String userId) {
        log.info("WorkerClientService: Get all equations by user id {} request to worker", userId);
        ResponseEntity<PaymentDTO> response = restTemplate
            .exchange(
                uri7 + userId,
                HttpMethod.GET,
                null,
                PaymentDTO.class
            );

        return response.getBody();
    }
}
