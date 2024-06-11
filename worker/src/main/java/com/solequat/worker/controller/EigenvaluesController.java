package com.solequat.worker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.CalculationDataIdDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.IntermediateResultDTO;
import com.solequat.worker.service.EigenvaluesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class EigenvaluesController {

    private final EigenvaluesService eigenvaluesService;

    public EigenvaluesController(EigenvaluesService eigenvaluesService) {
        this.eigenvaluesService = eigenvaluesService;
    }

    @PostMapping("/eigenvalues")
    public ResponseEntity<IntermediateResultDTO> calculateEigenvalues
        (@RequestBody CalculationDataIdDTO eigenvaluesIdDTO) throws Exception {
        log.info("EigenvaluesController: calculate eigenvalues");
        return ResponseEntity.status(HttpStatus.OK).body(
            eigenvaluesService.calculateEigenvaluesFirstStage(eigenvaluesIdDTO));
    }

    @GetMapping("/eigenvalues/{id}")
    public ResponseEntity<CalculationDataResultDTO> getEigenvaluesById(@PathVariable String id) {
        log.info("EigenvaluesController: Get eigenvalues by id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(
            eigenvaluesService.getEigenvaluesById(id));
    }

    @GetMapping("/all/eigenvalues/{userId}")
    public ResponseEntity<List<CalculationDataHistoryDTO>> getAllEigenvalues(@PathVariable String userId) {
        log.info("EigenvaluesController: Get all eigenvalues of the user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(
            eigenvaluesService.getAllEigenvaluesByUserId(userId));
    }

    @GetMapping("/eigenvalues/result/{id}")
    public  byte[] getEigenvaluesResultById(@PathVariable String id) throws Exception {
        log.info("EigenvaluesController: Get eigenvalues result by id {}", id);
        return eigenvaluesService.getEigenvaluesResultById(id);
    }

    @GetMapping("/eigenvalues/matrix/{id}")
    public byte[] getEigenvaluesMatrixById(@PathVariable String id) throws Exception {
        log.info("EigenvaluesController: Get eigenvalues matrix by id {}", id);
        return eigenvaluesService.getEigenvaluesMatrixById(id);
    }
}
