package com.solequat.businesslogic.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;
import com.core.dto.PaymentDTO;
import com.core.entity.User;
import com.solequat.businesslogic.service.UserService;
import com.solequat.businesslogic.service.WorkerClientService;

import com.sun.istack.NotNull;

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
        (@RequestBody MultipartFile matrixFile, MultipartFile vectorFile, @NotNull Principal principal)
        throws IOException {
        log.info("WorkerClientController: Calculate equation");
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.calculateEquationFirstStage(matrixFile, vectorFile, user.getId()));
    }

    @GetMapping("/equation")
    public ResponseEntity<EquationResultDTO> getEquationById(@RequestParam String id) {
        log.info("WorkerClientController: Get equation by id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.getEquationById(id));
    }

    @GetMapping("/equations")
    public ResponseEntity<List<EquationHistoryDTO>> getAllEquations(@NotNull Principal principal) {
        String email = principal.getName();
        log.info("WorkerClientController: Get all equations of the user with email {}", email);
        User user = userService.findUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.getAllEquationsByUserId(user.getId()));
    }

    @GetMapping("/result")
    public ResponseEntity<Resource> getResultById(@RequestParam String id) {
        log.info("WorkerClientController: Get result by id {}", id);

        byte[] csvBytes = workerClientService.getResultById(id);

        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.csv");

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(csvBytes.length)
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(resource);
    }

    @GetMapping("/vector")
    public ResponseEntity<Resource> getVectorById(@RequestParam String id) {
        log.info("WorkerClientController: Get vector by id {}", id);

        byte[] csvBytes = workerClientService.getVectorById(id);

        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vector.csv");

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(csvBytes.length)
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(resource);
    }

    @GetMapping("/matrix")
    public ResponseEntity<Resource> getMatrixById(@RequestParam String id) {
        log.info("WorkerClientController: Get matrix by id {}", id);

        byte[] csvBytes = workerClientService.getMatrixById(id);

        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=matrix.csv");

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(csvBytes.length)
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(resource);
    }

    @GetMapping("/payment")
    public ResponseEntity<PaymentDTO> getAllEquationsByUserIdAndIsPaid(@NotNull Principal principal) {
        String email = principal.getName();
        log.info("WorkerClientController: Get all equations of the user with email and isPaid=false {}", email);
        User user = userService.findUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(
            workerClientService.getAllEquationsByUserIdAndIsPaid(user.getId()));
    }
}
