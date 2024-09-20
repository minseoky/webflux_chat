package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryChatMessageRepository implements ChatMessageRepository {

    private final Map<String, List<String>> chatRooms = new HashMap<>();

    @Override
    public Mono<Void> saveMessage(String roomId, String username, String message) {
        chatRooms.computeIfAbsent(roomId, k -> new ArrayList<>()).add(username + ": " + message);
        return Mono.empty();
    }

    @Override
    public Flux<String> getMessages(String roomId) {
        List<String> messages = chatRooms.getOrDefault(roomId, new ArrayList<>());
        return Flux.fromIterable(messages);
    }
}
