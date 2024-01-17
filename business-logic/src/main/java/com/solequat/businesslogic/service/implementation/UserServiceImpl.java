package com.solequat.businesslogic.service.implementation;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.core.dto.UsernameDTO;
import com.core.repository.UserRepository;
import com.core.entity.User;
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
        log.debug(String.format("UserServiceImpl: enabling user with the email %s", email));
        userRepository.enableUser(email);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info(String.format("UserServiceImpl: find user with the email %s", email));
        return userRepository.findByEmail(email).
            orElseThrow(EntityNotFoundException::new);
    }

    public UsernameDTO updateUsername(User user, UsernameDTO usernameDTO) {
        log.info(String.format("UserServiceImpl: updating username with %s %s",
            usernameDTO.getFirstName(), usernameDTO.getLastName()));

        user.setFirstName(usernameDTO.getFirstName());
        user.setLastName(usernameDTO.getLastName());
        userRepository.save(user);
        return usernameDTO;
    }

}