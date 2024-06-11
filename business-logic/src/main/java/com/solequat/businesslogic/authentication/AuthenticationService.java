package com.solequat.businesslogic.authentication;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.mail.SendFailedException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.entity.ConfirmationToken;
import com.core.entity.Role;
import com.core.entity.User;
import com.core.repository.UserRepository;
import com.google.common.io.Files;
import com.solequat.businesslogic.config.JwtUtil;
import com.solequat.businesslogic.service.ConfirmationTokenService;
import com.solequat.businesslogic.service.EmailSenderService;
import com.solequat.businesslogic.service.UserService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final static String TOKEN_ALREADY_CONFIRMED = "AuthenticationService: token %s is already confirmed";
    private final static String TOKEN_EXPIRED = "AuthenticationService: token %s expired";
    private final static String LOGIN_ROUTE = "<meta http-equiv=\"refresh\" content=\"0;" +
        " url=http://localhost:3000/login\" />";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private  final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final EmailSenderService emailSender;

    public AuthenticationResponse register(RegisterRequest request) throws IOException, SendFailedException {

        log.info(String.format("AuthenticationService: registering user with email %s", request.getEmail()));

        boolean userExists = userRepository
            .findByEmail(request.getEmail())
            .isPresent();

        if (userExists) {
            log.error(String.format("AuthenticationService: email %s already taken", request.getEmail()));
            throw new RuntimeException();
        }

        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        userRepository.save(user);

        String jwtToken = jwtUtil.generateToken(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(
            jwtToken,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(6),
            user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8765/api/v1/registration/confirm?token=" + jwtToken;

        emailSender.send(
            request.getEmail(),
            buildEmail(link));

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
    }

    @Transactional
    public String confirmToken(String token) {
        log.info(String.format("AuthenticationService: confirming token %s", token));
        ConfirmationToken confirmationToken = confirmationTokenService
            .getToken(token)
            .orElseThrow(() -> new RuntimeException("AuthenticationService: Error with token " + token));

        if (confirmationToken.getConfirmedAt() != null) {
            log.error(String.format(TOKEN_ALREADY_CONFIRMED, token));
            throw new RuntimeException();
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error(String.format(TOKEN_EXPIRED, token));
            throw new RuntimeException();
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(
            confirmationToken.getUser().getEmail());

        return LOGIN_ROUTE;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow();

        String jwtToken = jwtUtil.generateToken(user);
        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private String buildEmail(String link) throws IOException {
        String date = "\n" + LocalDateTime.now().getMonth().getDisplayName(TextStyle.FULL, Locale.US)
            + " " + LocalDateTime.now().getDayOfMonth()
            + ", " + LocalDateTime.now().getYear();

        StringBuilder email = new StringBuilder(Files
            .asCharSource(new File("business-logic/src/main/resources/templates/emailConfirmationLetter.html"), StandardCharsets.UTF_8)
            .read());

        email
            .insert(email.indexOf("Project") + 3, date)
            .insert(email.indexOf("href=\"\"") + 6, link);

        return email.toString();
    }
}
