package com.example.demo.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ChatMessageRepository {
    Mono<Void> saveMessage(String roomId, String username, String message);
    Flux<String> getMessages(String roomId);
}
