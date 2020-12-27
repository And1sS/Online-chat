package com.and1ss.onlinechat.api.ws.base;

import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.CharacterCodingException;

public interface CrudRequestHandler<T> {
    void handleCreationRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<T> message
    ) throws JsonProcessingException, CharacterCodingException;

    void handleUpdateRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<T> message
    ) throws JsonProcessingException;

    void handleDeleteRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<T> message
    ) throws JsonProcessingException;

    void handleReadRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<T> message
    );
}
