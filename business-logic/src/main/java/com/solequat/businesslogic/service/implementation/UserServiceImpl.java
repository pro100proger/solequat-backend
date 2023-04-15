package com.solequat.businesslogic.service.implementation;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.solequat.businesslogic.config.ApplicationConfig;
import com.solequat.businesslogic.dto.UserDTO;
import com.solequat.businesslogic.entity.ConfirmationToken;
import com.solequat.businesslogic.entity.User;
//import com.solequat.businesslogic.mapper.UserMapper;
import com.solequat.businesslogic.repository.UserRepository;
import com.solequat.businesslogic.service.ConfirmationTokenService;
import com.solequat.businesslogic.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public void enableUser(String email) {
        log.debug(String.format("enabling user with the email %s", email));
        userRepository.enableUser(email);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info(String.format("find user with the email %s", email));
        return userRepository.findByEmail(email).
            orElseThrow(EntityNotFoundException::new);
    }
}