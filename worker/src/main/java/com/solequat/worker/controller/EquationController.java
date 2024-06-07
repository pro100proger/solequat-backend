package com.solequat.worker.controller;

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
import com.core.dto.PaymentDTO;
import com.solequat.worker.service.EquationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class EquationController {
    private final EquationService equationService;

    @Autowired
    public EquationController(EquationService equationService) {
        this.equationService = equationService;
    }

    @PostMapping("/equation")
    public ResponseEntity<EquationIntermediateResultDTO> calculateEquation
        (@RequestBody EquationIdDTO equationIdDTO) throws Exception {
        log.info("EquationController: calculate equation");
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.calculateEquationFirstStage(equationIdDTO));
    }


    @GetMapping("/equation/{id}")
    public ResponseEntity<EquationResultDTO> getEquationById(@PathVariable String id) {
        log.info("EquationController: Get equation by id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.getEquationById(id));
    }

    @GetMapping("/equations/{userId}")
    public ResponseEntity<List<EquationHistoryDTO>> getAllEquations(@PathVariable String userId) {
        log.info("EquationController: Get all equations of the user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.getAllEquationsByUserId(userId));
    }

    @GetMapping("/result/{id}")
    public  byte[] getResultById(@PathVariable String id) throws Exception {
        log.info("EquationController: Get result by id {}", id);
        return equationService.getResultById(id);
    }

    @GetMapping("/vector/{id}")
    public byte[] getVectorById(@PathVariable String id) throws Exception {
        log.info("EquationController: Get vector by id {}", id);
        return equationService.getVectorById(id);
    }

    @GetMapping("/matrix/{id}")
    public byte[] getMatrixById(@PathVariable String id) throws Exception {
        log.info("EquationController: Get matrix by id {}", id);
        return equationService.getMatrixById(id);
    }

    @GetMapping("/payment/{userId}")
    public ResponseEntity<PaymentDTO> getAllEquationsByUserIdAndIsPaid(@PathVariable String userId) {
        log.info("EquationController: Get all equations of the user with id {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(
            equationService.getAllEquationsByUserIdAndIsPaid(userId));
    }
}
