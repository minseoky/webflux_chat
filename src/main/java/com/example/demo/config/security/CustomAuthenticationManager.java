package com.example.demo.config.security;

import com.example.demo.exception.exceptions.LoginFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return reactiveUserDetailsService.findByUsername(authentication.getName())
                .doOnNext(userDetails -> log.info("Retrieved user: {}, password: {}", userDetails.getUsername(), userDetails.getPassword()))
                .filter(userDetails -> {
                    boolean matches = passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword());
                    log.info("Password matches: {}", matches);
                    return matches;
                })
                .map(userDetails -> (Authentication) new UsernamePasswordAuthenticationToken(
                        authentication.getName(), null, userDetails.getAuthorities()))
                .switchIfEmpty(Mono.error(new LoginFailedException("Invalid username or password")));

    }
}