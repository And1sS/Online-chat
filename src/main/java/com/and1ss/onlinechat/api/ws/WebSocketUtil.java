package com.and1ss.onlinechat.api.ws;

import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public class WebSocketUtil {
    public static UUID getUserIdFromSession(WebSocketSession session) {
        return UUID.fromString((String) session.getAttributes().get("userId"));
    }
}
