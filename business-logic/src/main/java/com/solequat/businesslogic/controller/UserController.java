package com.solequat.businesslogic.controller;

import java.io.IOException;
import java.security.Principal;

import javax.mail.SendFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.dto.EmailDTO;
import com.core.dto.PasswordDTO;
import com.core.dto.UserEmailDTO;
import com.core.dto.UsernameDTO;
import com.core.entity.User;
import com.solequat.businesslogic.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/credentials")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/username")
    public ResponseEntity<UsernameDTO> getUsername(Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("UserController: get the user's username with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.getUsername(user));
    }

    @GetMapping("/email")
    public ResponseEntity<EmailDTO> getEmail(Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("UserController: updating the user's username with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.getEmail(user));
    }

    @PutMapping("/username")
    public ResponseEntity<UsernameDTO> updateUsername(Principal principal, @RequestBody UsernameDTO usernameDTO) {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("UserController: updating the user's username with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.updateUsername(user, usernameDTO));
    }

    @PutMapping("/email")
    public ResponseEntity<EmailDTO> updateEmail(Principal principal, @RequestBody UserEmailDTO userEmailDTO)
        throws SendFailedException, IOException {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("UserController: updating the user's email with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.updateEmail(user, userEmailDTO));
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(Principal principal, @RequestBody PasswordDTO passwordDTO) {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("UserController: updating the user's password with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.updatePassword(user, passwordDTO));
    }
}
