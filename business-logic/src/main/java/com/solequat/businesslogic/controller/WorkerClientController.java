package com.solequat.businesslogic.controller;

import java.security.Principal;
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

import com.core.dto.EquationDTO;
import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;
import com.core.entity.User;
import com.solequat.businesslogic.service.WorkerClientService;
import com.solequat.businesslogic.service.UserService;
import com.sun.istack.NotNull;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("api/v1")
public class WorkerClientController {

    private final UserService userService;
    private final WorkerClientService workerClientService;

    @Autowired
    public WorkerClientController(UserService userService, WorkerClientService workerClientService) {
        this.userService = userService;
        this.workerClientService = workerClientService;
    }

    @PostMapping("/equation")
    public ResponseEntity<EquationIntermediateResultDTO> calculateEquation
        (@RequestBody EquationDTO equationDTO, @NonNull Principal principal) {
        log.info("EquationController: Calculate equation");
        String email = principal.getName();
        User user = userService.findUserByEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.calculateEquationFirstStage(equationDTO, user.getId()));
    }

    @GetMapping("/equation/{id}")
    public ResponseEntity<EquationResultDTO> getEquationById(@PathVariable String id) {
        log.info("EquationController: Get equation by id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.getEquationById(id));
    }

    @GetMapping("/equations")
    public ResponseEntity<List<EquationHistoryDTO>> getAllEquations(@NotNull Principal principal) {
        String email = principal.getName();
        log.info("EquationController: Get all equations of the user with email {}", email);
        User user = userService.findUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.getAllEquationsByUserId(user.getId()));
    }
}
