package worker.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIdDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;
import com.core.entity.Equation;

import lombok.extern.slf4j.Slf4j;
import worker.repository.EquationRepository;
import worker.service.EquationService;

@Slf4j
@Service
public class EquationServiceImpl implements EquationService {

    private final EquationRepository equationRepository;

    @Autowired
    public EquationServiceImpl(EquationRepository equationRepository) {
        this.equationRepository = equationRepository;
    }

    public EquationIntermediateResultDTO calculateEquationFirstStage(EquationIdDTO equationIdDTO) {
        log.info("EquationService: calculate equation first stage");

        LocalDateTime startCalculation = LocalDateTime.now();

        Equation equation = equationRepository.getEquationById(equationIdDTO.getId());

        EquationIntermediateResultDTO firstStageResult = new EquationIntermediateResultDTO();
        firstStageResult.setStartCalculation(startCalculation);
        firstStageResult.setEquationName(equation.getEquationName());

        long start = System.currentTimeMillis();

        new Thread(() -> {
            try {
                calculateEquationSecondStage(equation, start);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        firstStageResult.setId(equation.getId());
        return firstStageResult;
    }

    public EquationResultDTO calculateEquationSecondStage(Equation equation, long start) throws InterruptedException {
        log.info("EquationService: calculate equation second stage");

        EquationResultDTO secondStageResult = new EquationResultDTO();

        Thread.sleep(100);
        //TODO calculate equation



        long finish = System.currentTimeMillis();
        long duration = finish - start;

        secondStageResult.setDuration(duration);
        equation.setDuration(duration);

        LocalDateTime endCalculation =  LocalDateTime.now().plusSeconds(duration);

        secondStageResult.setEndCalculation(endCalculation);
        equation.setEndCalculation(endCalculation);

        equationRepository.save(equation);
        log.info("EquationService: end calculation");

        return secondStageResult;
    }

    public EquationResultDTO getEquationById(String id) {
        Equation equation = equationRepository.getEquationById(id);
        log.info("EquationService: Get equation by id in service");
        if (!equationRepository.existsById(id)) {
            log.error(String.format("EquationService: Equation not found by id: %s", id));
            throw new RuntimeException();
        }
        EquationResultDTO equationResultDTO = new EquationResultDTO();
        equationResultDTO.setDuration(equation.getDuration());
        equationResultDTO.setEndCalculation(equation.getEndCalculation());

        return equationResultDTO;
    }


    public List<EquationHistoryDTO> getAllEquationsByUserId(String userId) {
        List<Equation> equations = equationRepository.getAllEquationsByUserId(userId).orElseThrow(EntityNotFoundException::new);
        log.info("EquationService: Get all equations by user id {} ", userId);

        return equations.stream()
            .map(equation -> {
                EquationHistoryDTO equationHistoryDTO = new EquationHistoryDTO();
                equationHistoryDTO.setStartCalculation(equation.getStartCalculation());
                equationHistoryDTO.setEndCalculation(equation.getEndCalculation());
                equationHistoryDTO.setDuration(equation.getDuration());
                return equationHistoryDTO;
            })
            .collect(Collectors.toList());
    }
}
