package com.and1ss.onlinechat.api.ws.handlers;

import com.and1ss.onlinechat.services.dto.PrivateMessageCreationDTO;
import com.and1ss.onlinechat.services.dto.PrivateMessageRetrievalDTO;
import com.and1ss.onlinechat.api.ws.base.AbstractWebSocketHandler;
import com.and1ss.onlinechat.api.ws.base.CrudRequestHandler;
import com.and1ss.onlinechat.api.ws.base.WebSocketMessageMapper;
import com.and1ss.onlinechat.api.ws.base.WebSocketMessageType;
import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.and1ss.onlinechat.domain.PrivateMessage;
import com.and1ss.onlinechat.services.PrivateChatMessageService;
import com.and1ss.onlinechat.services.PrivateChatService;
import com.and1ss.onlinechat.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.UUID;

@Service
public class PrivateMessageRequestHandler implements CrudRequestHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    private final PrivateChatService privateChatService;

    private final PrivateChatMessageService privateChatMessageService;

    private final UserService userService;

    public PrivateMessageRequestHandler(
            PrivateChatService privateChatService,
            PrivateChatMessageService privateChatMessageService,
            UserService userService
    ) {
        this.privateChatService = privateChatService;
        this.privateChatMessageService = privateChatMessageService;
        this.userService = userService;
    }

    @Override
    public void handleCreationRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) throws JsonProcessingException {
        final var messageDTO = mapper.convertValue(
                message.getPayload(),
                PrivateMessageCreationDTO.class
        );
        final var userId = UUID.fromString((String) session.getAttributes().get("userId"));
        final var createdMessage = createNewMessage(messageDTO, userId);
        final var chat = createdMessage.getChat();
        final var usersIds = Arrays.asList(
                chat.getUser1().getId(),
                chat.getUser2().getId()
        );
        final var savedMessageDTO = PrivateMessageRetrievalDTO.fromPrivateMessage(createdMessage);
        final var webSocketMessage = new ChatWebSocketMessage(WebSocketMessageType.PRIVATE_MESSAGE_CREATE, savedMessageDTO);
        final var binaryMessage = WebSocketMessageMapper.webSocketMessageToBinaryMessage(webSocketMessage);
        webSocketHandler.sendToUsersWhoseIdIn(usersIds, binaryMessage);
    }

    @Transactional
    public PrivateMessage createNewMessage(
            PrivateMessageCreationDTO messageCreationDTO,
            UUID userId
    ) {
        final var authorizedUser = userService.findUserById(userId);
        final var privateChat = privateChatService
                .getPrivateChatById(messageCreationDTO.getChatId(), authorizedUser);

        final var message = PrivateMessage.builder()
                .author(authorizedUser)
                .chat(privateChat)
                .contents(messageCreationDTO.getContents())
                .build();

        return privateChatMessageService
                .addMessage(privateChat, message, authorizedUser);
    }

    @Override
    public void handleUpdateRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) {

    }

    @Override
    public void handleDeleteRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) throws JsonProcessingException {

    }

    @Override
    public void handleReadRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) {

    }
}
