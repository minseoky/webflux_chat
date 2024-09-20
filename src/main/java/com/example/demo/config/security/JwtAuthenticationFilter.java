package com.example.demo.config.security;

import com.example.demo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 인증을 적용하지 않을 경로 설정
        if (path.startsWith("/api/v1/user/login") || path.startsWith("/api/v1/user/registration")) {
            log.info("pass: {}", path);
            return chain.filter(exchange);  // 필터를 적용하지 않고 바로 체인 실행
        }

        // 인증을 적용할 경우 기존 로직 수행
        String token = extractTokenFromCookie(exchange.getRequest());
        log.info("token: {}", token);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String username = jwtUtil.extractUsername(token);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            log.info("Authentication Success");
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
        }

        log.info("Authentication Failed");
        return chain.filter(exchange);
    }


    private String extractTokenFromCookie(ServerHttpRequest request) {
        return Optional.ofNullable(request.getCookies().getFirst("jwt"))
                .map(HttpCookie::getValue)
                .orElse(null);
    }
}
