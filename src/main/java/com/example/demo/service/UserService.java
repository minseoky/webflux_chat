package com.example.demo.service;

import com.example.demo.dto.data.FindUserResponseData;
import com.example.demo.dto.request.UserLoginRequestDto;
import com.example.demo.dto.request.UserRegistrationRequestDto;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<FindUserResponseData> findUserByUsername(String username);

    Mono<Void> userRegistration(UserRegistrationRequestDto requestDto);

    Mono<Void> userLogin(UserLoginRequestDto requestDto, ServerWebExchange exchange);

}
