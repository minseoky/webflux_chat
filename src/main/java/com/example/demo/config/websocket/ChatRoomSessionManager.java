package com.example.demo.config.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRoomSessionManager {

    // 채팅방별로 세션을 관리
    private final Map<String, Map<String, WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    public void addSession(String roomId, String username, WebSocketSession session) {
        chatRooms.computeIfAbsent(roomId, key -> new ConcurrentHashMap<>()).put(username, session);
    }

    public void removeSession(String roomId, String username) {
        Map<String, WebSocketSession> roomSessions = chatRooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(username);
            if (roomSessions.isEmpty()) {
                chatRooms.remove(roomId);
            }
        }
    }

    public Map<String, WebSocketSession> getSessions(String roomId) {
        return chatRooms.getOrDefault(roomId, new ConcurrentHashMap<>());
    }
}
