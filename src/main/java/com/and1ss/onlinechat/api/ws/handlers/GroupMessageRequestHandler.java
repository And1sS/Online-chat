package com.and1ss.onlinechat.api.ws.handlers;

import com.and1ss.onlinechat.api.ws.base.*;
import com.and1ss.onlinechat.api.ws.dto.GroupMessageCreationDTO;
import com.and1ss.onlinechat.api.ws.dto.GroupMessageRetrievalDTO;
import com.and1ss.onlinechat.api.ws.dto.WebSocketMessage;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.CharacterCodingException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupMessageRequestHandler implements CrudRequestHandler<Object> {

    private final ObjectMapper mapper = new ObjectMapper();

    private final GroupChatService groupChatService;

    private final GroupChatMessageService groupChatMessageService;

    private final UserService userService;

    @Autowired
    public GroupMessageRequestHandler(
            GroupChatService groupChatService,
            GroupChatMessageService groupChatMessageService,
            UserService userService
    ) {
        this.groupChatService = groupChatService;
        this.groupChatMessageService = groupChatMessageService;
        this.userService = userService;
    }

    @Override
    public void handleCreationRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            WebSocketMessage<Object> message
    ) throws JsonProcessingException, CharacterCodingException {
        final var messageDTO = parseRawMessageToGroupMessageCreationDTO(message);
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        AccountInfo authorizedUser = userService.findUserById(userId);
        GroupChat groupChat = groupChatService.getGroupChatById(messageDTO.getChatId(), authorizedUser);
        GroupMessage groupMessage = GroupMessage.builder()
                .author(authorizedUser)
                .chat(groupChat)
                .contents(messageDTO.getContents())
                .build();

        GroupMessage savedMessage = groupChatMessageService
                .addMessage(groupChat, groupMessage, authorizedUser);

        final var savedMessageDTO = GroupMessageRetrievalDTO.fromGroupMessage(savedMessage);
        final var usersIds = groupChatService.getGroupChatMembersIds(groupChat, authorizedUser)
                .stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        final var webSocketMessage = new WebSocketMessage(WebSocketMessageType.GROUP_MESSAGE_CREATE, savedMessageDTO);
        final var binaryMessage = WebSocketMessageMapper.webSocketMessageToBinaryMessage(webSocketMessage);
        WebSocketHandler.sendToUsersWhoseIdIn(usersIds, binaryMessage);
    }

    private GroupMessageCreationDTO parseRawMessageToGroupMessageCreationDTO(
            WebSocketMessage<Object> message) {
        return mapper.convertValue(message.getPayload(), GroupMessageCreationDTO.class);
    }

    @Override
    public void handleUpdateRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            WebSocketMessage<Object> message
    ) {

    }

    @Override
    public void handleDeleteRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            WebSocketMessage<Object> message
    ) {

    }

    @Override
    public void handleReadRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            WebSocketMessage<Object> message
    ) {

    }
}
