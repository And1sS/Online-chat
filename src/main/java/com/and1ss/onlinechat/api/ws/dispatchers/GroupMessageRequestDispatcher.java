package com.and1ss.onlinechat.api.ws.dispatchers;

import com.and1ss.onlinechat.api.ws.base.AbstractWebSocketHandler;
import com.and1ss.onlinechat.api.ws.dto.WebSocketMessage;
import com.and1ss.onlinechat.api.ws.handlers.GroupMessageRequestHandler;
import com.and1ss.onlinechat.exceptions.InternalServerException;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.CharacterCodingException;

@Component
public class GroupMessageRequestDispatcher {
    private final GroupMessageRequestHandler requestHandler;

    @Autowired
    public GroupMessageRequestDispatcher(GroupMessageRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void dispatchMessage(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            WebSocketMessage<Object> message
    ) throws JsonProcessingException, CharacterCodingException {
        var messageType = message.getMessageType();
        switch (messageType) {
            case GROUP_MESSAGE_CREATE ->
                    requestHandler.handleCreationRequest(session, webSocketHandler, message);
            default ->
                    throw new InternalServerException("Message type " + messageType + " is not supported");
        }
    }
}
