package com.solequat.businesslogic.authentication;

import java.io.IOException;

import javax.mail.SendFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/registration")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody RegisterRequest request
    ) throws IOException, SendFailedException {
        log.info(String.format("Controller: registering user with email %s", request.getEmail()));
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        log.info(String.format("Controller: confirming token %s", token));
        return ResponseEntity.ok(authenticationService.confirmToken(token));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
        @RequestBody AuthenticationRequest request
        ) {
        log.info(String.format("Controller: authenticate user with email %s", request.getEmail()));
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("token") String token) {
        log.info(String.format("Controller: validate token %s", token));
        return authenticationService.validateToken(token);
    }
}
