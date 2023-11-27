package com.example.demo.service;

import com.example.demo.config.jwt.JwtProvider;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.errors.exception.InvalidCredentialsException;
import com.example.demo.model.Secret;
import com.example.demo.model.User;
import com.example.demo.payload.AuthResponse;
import com.example.demo.payload.BaseApiResponse;
import com.example.demo.payload.RegisterResponse;
import com.example.demo.repository.SecretRepository;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final SecretRepository secretRepository;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final JwtService jwtService;

    public BaseApiResponse login(LoginRequestDto loginRequestDto) {
        User user = userService.getUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        return new AuthResponse(jwtProvider.generateToken(user));
    }

    public BaseApiResponse register(RegisterRequestDto registerRequestDto){
        if (userRepository.existsByEmail(registerRequestDto.getEmail())){
            throw new InvalidCredentialsException("User already exist");
        }

        userService.createUser(registerRequestDto);
        return new RegisterResponse("User is registered successfully!");
    }

    public BaseApiResponse logout(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        Secret secret = secretRepository.getBySecretText(jwtProvider.extractSecret(token)).orElseThrow(() ->
                new InvalidCredentialsException("Invalid token"));
        secret.setIsActive(Boolean.FALSE);
        secretRepository.saveAndFlush(secret);
        return new BaseApiResponse(200, "Success");
    }
}
