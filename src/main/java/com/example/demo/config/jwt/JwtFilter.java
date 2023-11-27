package com.example.demo.config.jwt;

import com.example.demo.model.Secret;
import com.example.demo.repository.SecretRepository;
import com.example.demo.service.AuthUserDetailsService;
import com.example.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static io.jsonwebtoken.lang.Strings.hasText;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final AuthUserDetailsService userDetailsService;

    private final SecretRepository secretRepository;

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = jwtService.getTokenFromRequest(request);
        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getLoginFromToken(token);
            User user = (User) userDetailsService.loadUserByUsername(username);
            Optional<Secret> secret = secretRepository.getBySecretText(jwtProvider.extractSecret(token));
            if (secret.isPresent() && (secret.get().getIsActive().equals(Boolean.TRUE))) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }

//    private String getTokenFromRequest(HttpServletRequest request) {
//        String bearer = request.getHeader("Authorization");
//        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
//            return bearer.substring(7);
//        }
//        return null;
//    }
}
