package com.solequat.businesslogic.service.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import javax.mail.SendFailedException;
import javax.persistence.EntityNotFoundException;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.dto.EmailDTO;
import com.core.dto.PasswordDTO;
import com.core.dto.UserEmailDTO;
import com.core.dto.UsernameDTO;
import com.core.entity.ConfirmationToken;
import com.core.repository.ConfirmationTokenRepository;
import com.core.repository.UserRepository;
import com.core.entity.User;
import com.solequat.businesslogic.config.ApplicationConfig;
import com.solequat.businesslogic.config.JwtUtil;
import com.solequat.businesslogic.service.ConfirmationTokenService;
import com.solequat.businesslogic.service.EmailSenderService;
import com.solequat.businesslogic.service.UserService;
import com.solequat.businesslogic.validator.PasswordValidator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailSender;
    private final ApplicationConfig applicationConfig;
    private final PasswordValidator passwordValidator;


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

    @Override
    public UsernameDTO getUsername(User user) {
        log.info(String.format("UserServiceImpl: get the user's username with the email %s", user.getEmail()));
        return new UsernameDTO(user.getFirstName(), user.getLastName());
    }

    @Override
    public EmailDTO getEmail(User user) {
        log.info(String.format("UserServiceImpl: get the user's email with the email %s", user.getEmail()));
        return new EmailDTO(user.getEmail());
    }

    @Override
    public UsernameDTO updateUsername(User user, UsernameDTO usernameDTO) {
        log.info(String.format("UserServiceImpl: updating the user's username with %s %s",
            user.getFirstName(), user.getLastName()));

        user.setFirstName(usernameDTO.getFirstName());
        user.setLastName(usernameDTO.getLastName());
        userRepository.save(user);
        return usernameDTO;
    }

    @Override
    @Transactional
    public EmailDTO updateEmail(User user, UserEmailDTO userEmailDTO) throws IOException, SendFailedException {
        log.info(String.format("UserServiceImpl: updating the user's email with email %s", user.getEmail()));

        String oldEmailDB = findUserByEmail(user.getEmail()).getEmail();
        if (!Objects.equals(oldEmailDB, userEmailDTO.getOldEmail())) {
            throw new RuntimeException(
                String.format("The old email %s you entered does not match the old email in the database.",
                    userEmailDTO.getOldEmail()));
        }

        boolean isPresentButMe = Objects.equals(user.getEmail(), userEmailDTO.getNewEmail());
        if (!isPresentButMe) {
            if (userRepository.findByEmail(userEmailDTO.getNewEmail()).isPresent()) {
                throw new RuntimeException(
                    String.format("Email %s is already taken by another user.", userEmailDTO.getNewEmail()));
            }
            else {
                String jwt = jwtUtil.generateToken(user);
                if (confirmationTokenRepository.findByUserId(user.getId()).isPresent()) {
                    ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserId(user.getId()).get();
                    confirmationToken.setToken(jwt);
                    confirmationToken.setCreatedAt(LocalDateTime.now());
                    confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
                    confirmationToken.setConfirmedAt(null);
                    confirmationToken.setUser(user);

                    confirmationTokenService.saveConfirmationToken(confirmationToken);
                } else {
                    throw new RuntimeException(
                        String.format("Confirmation token with userId %s is not exist. ", user.getId()));
                }

                String link = "http://localhost:8765/api/v1/registration/confirm?token=" + jwt;

                emailSender.send(
                    userEmailDTO.getNewEmail(),
                    buildEmail(link));
            }
        }
        user.setIsActive(false);
        user.setEmail(userEmailDTO.getNewEmail());
        userRepository.save(user);

        return new EmailDTO(user.getEmail());
    }

    private String buildEmail(String link) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);
        String date = "\n" + LocalDateTime.now().format(formatter);

        StringBuilder email = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("business-logic/src/main/resources/templates/confirmationLetterChangeEmail.html"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                email.append(line).append(System.lineSeparator());
            }
        }

        email
            .insert(email.indexOf("Project") + 3, date)
            .insert(email.indexOf("href=\"\"") + 6, link);

        return email.toString();
    }

    @Override
    public String updatePassword(User user, PasswordDTO passwordDTO) throws ServiceException {
        log.info(String.format("UserServiceImpl: updating the user's password with email %s", user.getEmail()));

        boolean checkPasswords = applicationConfig.passwordEncoder().
            matches(passwordDTO.getOldPassword(), user.getPassword());

        if (checkPasswords) {
            if (passwordValidator.test(passwordDTO.getPassword())) {
                passwordDTO.setPassword(applicationConfig.passwordEncoder().encode(passwordDTO.getPassword()));
            } else {
                throw new ServiceException("UserServiceImpl: password must contain at least 8 characters (letters and numbers)");
            }
        } else {
            throw new ServiceException("UserServiceImpl: old password not matches with entered password ");
        }

        user.setPassword(passwordDTO.getPassword());
        userRepository.save(user);

        return "Your password successfully updated.";
    }

}