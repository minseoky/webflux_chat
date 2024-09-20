package com.example.demo.controller;

import com.example.demo.dto.data.FindUserResponseData;
import com.example.demo.dto.request.UserLoginRequestDto;
import com.example.demo.dto.request.UserRegistrationRequestDto;
import com.example.demo.dto.response.ResponseDto;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인 테스트", description = "로그인이 잘 되었는지 확인")
    @GetMapping("/test")
    public Mono<ResponseEntity<ResponseDto<Void>>> loginTest(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication) // Authentication 객체 추출
                .flatMap(authentication -> {
                    // 유저 정보 로그 출력
                    log.info("Authenticated User: {}", authentication.getName());

                    // 추가적으로 권한 정보도 확인 가능
                    log.info("Authorities: {}", authentication.getAuthorities().toString());

                    // 요청 헤더 확인
                    log.info("Headers: {}", exchange.getRequest().getHeaders().toString());

                    // 응답 반환
                    return Mono.just(ResponseDto.success());
                })
                .then(Mono.just(ResponseDto.success()));
    }

    @Operation(summary = "사용자 조회", description = "username으로 사용자 조회")
    @GetMapping("/username/{username}")
    public Mono<ResponseEntity<ResponseDto<FindUserResponseData>>> findUserByUsername(@PathVariable String username) {
        return userService.findUserByUsername(username)
                .map(ResponseDto::success);
    }

    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록")
    @PostMapping("/registration")
    public Mono<ResponseEntity<ResponseDto<Void>>> userRegistration(@RequestBody UserRegistrationRequestDto requestDto) {
        return userService.userRegistration(requestDto)
                .then(Mono.just(ResponseDto.success()));
    }

    @Operation(summary = "로그인", description = "로그인을 통해 JWT 획득")
    @PostMapping("/login")
    public Mono<ResponseEntity<ResponseDto<Void>>> userLogin(@RequestBody UserLoginRequestDto requestDto, ServerWebExchange exchange){
        return userService.userLogin(requestDto, exchange)
                .then(Mono.just(ResponseDto.success()));
    }

}
