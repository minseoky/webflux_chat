package com.example.demo.service;

import com.example.demo.config.security.CustomAuthenticationManager;
import com.example.demo.dto.data.FindUserResponseData;
import com.example.demo.dto.request.UserLoginRequestDto;
import com.example.demo.dto.request.UserRegistrationRequestDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.exceptions.LoginFailedException;
import com.example.demo.exception.exceptions.UserNotFoundException;
import com.example.demo.exception.exceptions.UsernameDuplicatedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Mono<FindUserResponseData> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new FindUserResponseData(user.getUsername(), user.getCreatedAt()))
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")));
    }

    public Mono<Void> userRegistration(UserRegistrationRequestDto requestDto) {
        return userRepository.findByUsername(requestDto.getUsername())
                .flatMap(existingUser -> Mono.error(new UsernameDuplicatedException("Username already exists")))
                .switchIfEmpty(
                        Mono.defer(
                                () -> userRepository.save(new UserEntity(requestDto.getUsername(), passwordEncoder.encode(requestDto.getPassword())))
                        )
                ).then();

    }


    public Mono<Void> userLogin(UserLoginRequestDto requestDto, ServerWebExchange exchange) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()))
                .flatMap(auth -> Mono.defer(() -> {
                    String token = jwtUtil.generateToken(auth.getName());

                    // Response 쿠키에 JWT 토큰 추가
                    ResponseCookie jwtCookie = jwtUtil.generateCookie(token);

                    // 쿠키 추가
                    exchange.getResponse().addCookie(jwtCookie);

                    return Mono.empty();
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

}
