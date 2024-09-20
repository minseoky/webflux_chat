package com.example.demo.config.websocket;

import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatRoomSessionManager chatRoomSessionManager;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // extract username from jwt
        String username = extractUsernameFromSession(session);
        if (username == null) {
            return session.close(); // JWT 인증 실패 시 WebSocket 세션 종료
        }

        // extract chatroomId
        String roomId = extractRoomIdFromSession(session, username);

        // add Session
        chatRoomSessionManager.addSession(roomId, username, session);

        // get previous chats from current chatroom
        return sendPreviousMessagesToUser(roomId, session)
                .thenMany(
                        session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .doOnNext(message -> {
                                    // 메시지 저장
                                    chatMessageRepository.saveMessage(roomId, username, message);

                                    // 채팅방 내 모든 사용자에게 메시지 브로드캐스트
                                    broadcastMessageToRoom(roomId, username, message);
                                })
                )
                .then()  // 이전처럼 본인에게 메시지를 다시 보내지 않도록 'send' 부분 없음
                .doFinally(signalType -> {
                    // 세션이 종료될 때 세션 제거
                    chatRoomSessionManager.removeSession(roomId, username);
                });
    }

    // send message for new user
    private Mono<Void> sendPreviousMessagesToUser(String roomId, WebSocketSession session) {
        // 이전 메시지들을 가져옴
        Flux<String> previousMessages = chatMessageRepository.getMessages(roomId);

        // 이전 메시지들을 하나씩 전송
        return session.send(
                previousMessages
                        .map(session::textMessage)
        ).then();
    }

    // 채팅방에 있는 모든 세션에 메시지를 전송
    private void broadcastMessageToRoom(String roomId, String username, String message) {
        chatRoomSessionManager.getSessions(roomId).forEach((user, userSession) -> {
            if (userSession.isOpen()/* && !user.equals(username)*/) {
                userSession.send(Mono.just(userSession.textMessage(username + ": " + message))).subscribe();
            }
        });
    }

    private String extractRoomIdFromSession(WebSocketSession session, String currentUser) {
        String path = session.getHandshakeInfo().getUri().getPath();
        // 경로에서 "/api/v1/chatwith/{username}"에서 상대방의 username 추출
        String[] segments = path.split("/");
        String targetUser = segments[segments.length - 1];

        log.info("Room prepared for ... From {} to {}", currentUser, targetUser);

        // 두 유저의 이름을 조합해 방 ID를 생성 (정렬을 통해 순서를 고정)
        return currentUser.compareTo(targetUser) < 0 ? currentUser + "_" + targetUser : targetUser + "_" + currentUser;
    }

    private String extractUsernameFromSession(WebSocketSession session) {
        // WebSocketSession에서 쿠키를 추출
        String cookieHeader = session.getHandshakeInfo().getHeaders().getFirst("Cookie");
        log.info("CookieHeader: {}", cookieHeader);

        if (cookieHeader != null) {
            // 쿠키를 세미콜론으로 분리하고 JWT 쿠키를 찾음
            String[] cookies = cookieHeader.split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("jwt=")) {
                    // "JWT=" 부분을 제거하고 JWT 토큰을 추출
                    String token = cookie.substring(4); // "jwt=" 제거
                    return jwtUtil.extractUsername(token); // jwt에서 사용자명 추출
                }
            }
        }

        return null;
    }
}