package com.solequat.businesslogic.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.solequat.businesslogic.config.JwtUtil;
import com.solequat.businesslogic.entity.Role;
import com.solequat.businesslogic.entity.User;
import com.solequat.businesslogic.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private  final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Service: register user.");
        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        userRepository.save(user);

        String jwtToken = jwtUtil.generateToken(user);
        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
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
}
