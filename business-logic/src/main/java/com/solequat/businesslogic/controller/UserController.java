package com.solequat.businesslogic.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.core.dto.UsernameDTO;
import com.core.entity.User;
import com.solequat.businesslogic.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/name")
    public ResponseEntity<UsernameDTO> updateUsername(Principal principal, @RequestBody UsernameDTO usernameDTO) {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("Controller: updating username with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.updateUsername(user, usernameDTO));
    }
}
