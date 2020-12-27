package com.and1ss.onlinechat.api.ws.base;

import com.and1ss.onlinechat.api.ws.dispatchers.GroupMessageRequestDispatcher;
import com.and1ss.onlinechat.api.ws.dispatchers.PrivateMessageRequestDispatcher;
import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.CharacterCodingException;
import java.util.HashMap;

@Slf4j
@Service
public class WebSocketHandler extends AbstractWebSocketHandler {

    private final GroupMessageRequestDispatcher groupMessageRequestDispatcher;
    private final PrivateMessageRequestDispatcher privateMessageRequestDispatcher;

    @Autowired
    public WebSocketHandler(
            GroupMessageRequestDispatcher groupMessageRequestDispatcher,
            PrivateMessageRequestDispatcher privateMessageRequestDispatcher
    ) {
        this.groupMessageRequestDispatcher = groupMessageRequestDispatcher;
        this.privateMessageRequestDispatcher = privateMessageRequestDispatcher;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage binaryMessage) {
        try {
            uncheckedHandleBinaryMessage(session, binaryMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void uncheckedHandleBinaryMessage(
            WebSocketSession session,
            BinaryMessage binaryMessage
    ) throws JsonProcessingException, CharacterCodingException {
        ObjectMapper mapper = new ObjectMapper();
        var typeRef = new TypeReference<HashMap<String, Object>>() {};

        String message = WebSocketMessageMapper.byteBufferToString(binaryMessage.getPayload());
        HashMap<String, Object> object = mapper.readValue(message, typeRef);

        var stringMessageType = (String) object.get("message_type");

        if (stringMessageType == null) {
            throw new BadRequestException("Incoming message type is not specified");
        }

        var payload = object.get("payload");

        if (payload == null) {
            throw new BadRequestException("Incoming message payload is not present");
        }

        WebSocketMessageType messageType;
        try {
            messageType = Enum.valueOf(WebSocketMessageType.class, stringMessageType);
        } catch (Exception e) {
            throw new BadRequestException("Invalid message type");
        }

        ChatWebSocketMessage<Object> partiallyParsedMessage =
                new ChatWebSocketMessage(messageType, payload);

        dispatchIncomingMessage(session, partiallyParsedMessage);
    }

    private void dispatchIncomingMessage(
            WebSocketSession session,
            ChatWebSocketMessage<Object> partiallyParsedMessage
    ) throws JsonProcessingException, CharacterCodingException {
        var messageType = partiallyParsedMessage.getMessageType();
        switch (messageType) {
            case GROUP_MESSAGE_CREATE, GROUP_MESSAGE_PATCH, GROUP_MESSAGE_DELETE ->
                groupMessageRequestDispatcher.dispatchMessage(session, this, partiallyParsedMessage);

            case PRIVATE_MESSAGE_CREATE, PRIVATE_MESSAGE_PATCH, PRIVATE_MESSAGE_DELETE ->
                privateMessageRequestDispatcher.dispatchMessage(session, this, partiallyParsedMessage);

            default -> throw new InternalServerException("Message type " + messageType.toString() + " is not supported");
        }
    }
}
