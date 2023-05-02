package com.solequat.businesslogic.service.implementation;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.core.entity.User;
import com.solequat.businesslogic.repository.UserRepository;
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