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
//    private final UserMapper userMapper;
    private final ConfirmationTokenService confirmationTokenService;
    private final ApplicationConfig applicationConfig;

    @Override
    public String signUpUser(UserDTO userDTO) {

//        log.info(String.format("Service: signing up user with the email %s", userDTO.getEmail()));
//
//        User user = userMapper.dtoToEntity(userDTO);
//        boolean userExists = userRepository
//            .findByEmail(userDTO.getEmail())
//            .isPresent();
//
//        if (userExists) {
//            log.error(String.format("Service: email %s already taken", userDTO.getEmail()));
//            throw new RuntimeException();
//        }
//
//        String encodedPassword = applicationConfig.passwordEncoder()
//            .encode(userDTO.getPassword());
//
//        user.setPassword(encodedPassword);
//
//        log.info(String.format("Service: saving user with the email %s", userDTO.getEmail()));
//        userRepository.save(user);

        String token = UUID.randomUUID().toString();
//
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//            token,
//            LocalDateTime.now(),
//            LocalDateTime.now().plusMinutes(15),
//            user
//        );
//
//        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

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