package com.solequat.businesslogic.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.core.dto.EquationDTO;
import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIdDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;
import com.core.entity.Equation;
import com.core.entity.User;
import com.solequat.businesslogic.repository.WorkerClientRepository;
import com.solequat.businesslogic.repository.UserRepository;
import com.solequat.businesslogic.service.WorkerClientService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkerClientServiceImpl implements WorkerClientService {

    private final WorkerClientRepository workerClientRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public WorkerClientServiceImpl(WorkerClientRepository workerClientRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.workerClientRepository = workerClientRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public EquationIntermediateResultDTO calculateEquationFirstStage(EquationDTO equationDTO, String userId) {
        log.info("WorkerClientService: Calculate request to worker");

        Equation equation = new Equation(equationDTO.getEquationName());

        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        equation.setUser(user);

        workerClientRepository.save(equation);

        EquationIdDTO equationIdDTO = new EquationIdDTO();
        equationIdDTO.setId(equation.getId());

        log.info("WorkerClientService: restTemplate request to worker");
        EquationIntermediateResultDTO equationIntermediateResultDTO = restTemplate.postForObject(
            "http://localhost:8765/worker/api/v1/equation",
            equationIdDTO,
            EquationIntermediateResultDTO.class);

        log.info("WorkerClientService: Response from worker");
        return equationIntermediateResultDTO;
    }

    public EquationResultDTO getEquationById(String id) {
        log.info("WorkerClientService: Get equation by id request to worker");

        return restTemplate.getForObject(
            "http://localhost:8765/worker/api/v1/equation/" + id,
            EquationResultDTO.class);
    }

    public List<EquationHistoryDTO> getAllEquationsByUserId(String userId) {
        log.info("WorkerClientService: Get all equations by user id {} request to worker", userId);
        return restTemplate.getForObject(
            "http://localhost:8765/worker/api/v1/equations/" + userId,
            null,
            new ParameterizedTypeReference<List<EquationHistoryDTO>>() {}
        );
    }
}
