package worker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIdDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;

import lombok.extern.slf4j.Slf4j;
import worker.service.EquationService;
//import com.core.CoreApplication;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class EquationController {
    private final EquationService equationService;
//    private final CoreApplication coreApplication = new CoreApplication();

    @Autowired
    public EquationController(EquationService equationService) {
        this.equationService = equationService;
    }

    @PostMapping("/equation")
    public ResponseEntity<EquationIntermediateResultDTO> calculateEquation
        (@RequestBody EquationIdDTO equationIdDTO) {
        log.info("EquationController: calculate equation");
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.calculateEquationFirstStage(equationIdDTO));
    }


    @GetMapping("/equation/{id}")
    public ResponseEntity<EquationResultDTO> getPermutationById(@PathVariable String id) {
        log.info("Get equation by id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.getEquationById(id));
    }

    @GetMapping("/equations/{userId}")
    public ResponseEntity<List<EquationHistoryDTO>> getAllPermutations(@PathVariable String userId) {
        log.info("Get all equations of the user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.getAllEquationsByUserId(userId));
    }
}
