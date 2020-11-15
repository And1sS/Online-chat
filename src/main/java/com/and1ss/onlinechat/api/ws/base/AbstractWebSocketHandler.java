package com.and1ss.onlinechat.api.ws.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractWebSocketHandler extends BinaryWebSocketHandler {
    private static ConcurrentHashMap<String, List<WebSocketSession>> activeSessions
            = new ConcurrentHashMap();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        Object userIdAttribute = session.getAttributes().get("userId");

        if (userIdAttribute == null || !(userIdAttribute instanceof String)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        onUserSubscribe(session, (String) userIdAttribute);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        if (status != CloseStatus.POLICY_VIOLATION) {
            try {
                onUserDisconnect(session, (String) (session.getAttributes().get("userId")));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private static void onUserSubscribe(WebSocketSession session, String userId) {
        var userSessions = activeSessions.get(userId);

        if (userSessions == null) {
            var list = new ArrayList<WebSocketSession>();
            list.add(session);

            activeSessions.put(userId, list);
        } else {
            if (!userSessions.contains(session)) {
                synchronized (userSessions) {
                    userSessions.add(session);
                }
            }
        }
    }

    private static void onUserDisconnect(WebSocketSession session, String userId) {
        var userSessions = activeSessions.get(userId);

        assert (userSessions != null);

        synchronized (userSessions) {
            userSessions.remove(session);
        }
    }

    public static void sendToUsersWhoseIdIn(List<String> usersIds, BinaryMessage message) {
        usersIds.forEach((userId) -> sendToAllUserSessions(userId, message));
    }

    private static void sendToAllUserSessions(String userId, BinaryMessage message) {
        var userSessions = activeSessions.get(userId);
        if (userSessions == null) {
            return;
        }

        userSessions.forEach(session -> {
            try {
                if (session != null && session.isOpen()) {
                    session.sendMessage(message);
                }

                if (session != null && !session.isOpen()) {
                    userSessions.remove(session);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
