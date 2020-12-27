package com.and1ss.onlinechat.api.ws.dispatchers;

import com.and1ss.onlinechat.api.ws.base.AbstractWebSocketHandler;
import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.and1ss.onlinechat.api.ws.handlers.PrivateMessageRequestHandler;
import com.and1ss.onlinechat.exceptions.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.CharacterCodingException;

@Component
public class PrivateMessageRequestDispatcher {
    private final PrivateMessageRequestHandler requestHandler;

    public PrivateMessageRequestDispatcher(
            PrivateMessageRequestHandler privateMessageRequestHandler
    ) {
        this.requestHandler = privateMessageRequestHandler;
    }

    public void dispatchMessage(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<Object> message
    ) throws JsonProcessingException, CharacterCodingException {
        var messageType = message.getMessageType();
        switch (messageType) {
            case PRIVATE_MESSAGE_CREATE ->
                    requestHandler.handleCreationRequest(session, webSocketHandler, message);
            case PRIVATE_MESSAGE_PATCH ->
                    requestHandler.handleUpdateRequest(session, webSocketHandler, message);
            case PRIVATE_MESSAGE_DELETE ->
                    requestHandler.handleDeleteRequest(session, webSocketHandler, message);
            default ->
                    throw new InternalServerException("Message type " + messageType + " is not supported");
        }
    }
}
