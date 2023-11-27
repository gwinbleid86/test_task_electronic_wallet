package com.example.demo.service;

import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.errors.exception.InvalidCredentialsException;
import com.example.demo.model.Role;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public User getUser(String email, String password) {
        Optional<User> optionalByEmail = userRepository.getByEmail(email);
        if (optionalByEmail.isEmpty() || !passwordEncoder.matches(password, optionalByEmail.get().getPassword())) {
            throw new InvalidCredentialsException("Incorrect email or password");
        }
        return optionalByEmail.get();
    }

    public User getUser(){
        var context = SecurityContextHolder.getContext().getAuthentication();
        var username = context.getName();
        return userRepository.getByEmail(username).orElseThrow(() -> new InvalidCredentialsException("Incorrect email or password"));
    }

    public void createUser(RegisterRequestDto requestDto) {
        User user = User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .enabled(Boolean.TRUE)
                .build();
        Role role = roleRepository.findByRole("AUTHENTICATED_USER");
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);
    }

    public void applyTransaction(User user, Transaction transaction){
        user.setBalance(user.getBalance() - transaction.getAmount());
        userRepository.save(user);
    }

}
