package com.and1ss.onlinechat.api.ws.handlers;

import com.and1ss.onlinechat.api.ws.WebSocketUtil;
import com.and1ss.onlinechat.services.dto.GroupMessageCreationDTO;
import com.and1ss.onlinechat.services.dto.GroupMessageRetrievalDTO;
import com.and1ss.onlinechat.api.ws.base.*;
import com.and1ss.onlinechat.api.ws.dto.WsGroupMessageDeleteDTO;
import com.and1ss.onlinechat.api.ws.dto.WsGroupMessagePatchDTO;
import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.utils.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
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
            ChatWebSocketMessage<?> message
    ) throws JsonProcessingException {
        GroupMessageCreationDTO messageDTO = mapper.convertValue(
                message.getPayload(), GroupMessageCreationDTO.class);
        UUID userId = WebSocketUtil.getUserIdFromSession(session);
        UUID chatId = messageDTO.getChatId();

        GroupMessageRetrievalDTO createdDTO = groupChatMessageService.addMessage(
                chatId, messageDTO, userId);

        List<UUID> usersIds = groupChatService.getGroupChatMembersIds(chatId, userId);
        ChatWebSocketMessage webSocketMessage = new ChatWebSocketMessage(
                WebSocketMessageType.GROUP_MESSAGE_CREATE, createdDTO);
        final var binaryMessage = WebSocketMessageMapper
                .webSocketMessageToBinaryMessage(webSocketMessage);
        webSocketHandler.sendToUsersWhoseIdIn(usersIds, binaryMessage);
    }

    @Override
    public void handleUpdateRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) throws JsonProcessingException {
        final var messageDTO = mapper.convertValue(
                message.getPayload(),
                WsGroupMessagePatchDTO.class
        );
        final var userId = UUID.fromString((String) session.getAttributes().get("userId"));
        final var pair = patchMessage(messageDTO, userId);
        final var savedMessageDTO = GroupMessageRetrievalDTO.fromGroupMessage(pair.getSecond());
        final var webSocketMessage = new ChatWebSocketMessage(WebSocketMessageType.GROUP_MESSAGE_PATCH, savedMessageDTO);
        final var binaryMessage = WebSocketMessageMapper.webSocketMessageToBinaryMessage(webSocketMessage);
        webSocketHandler.sendToUsersWhoseIdIn(pair.getFirst(), binaryMessage);
    }

    @Transactional
    public Pair<List<String>, GroupMessage> patchMessage(
            WsGroupMessagePatchDTO messageDTO,
            UUID userId
    ) {
        final var authorizedUser = userService.findUserById(userId);
        final var groupChat = groupChatService.getGroupChatById(messageDTO.getChatId(), authorizedUser.getId());
        final var message = GroupMessage.builder()
                .id(messageDTO.getMessageId())
                .author(authorizedUser)
                .chat(groupChat)
                .contents(messageDTO.getContents())
                .build();

        final var patchedMessage = groupChatMessageService
                .patchMessage(groupChat, message, authorizedUser);
        final var usersIds = getGroupChatMembersIds(groupChat, authorizedUser);
        return new Pair(usersIds, patchedMessage);
    }

    @Override
    public void handleDeleteRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<Object> message
    ) throws JsonProcessingException {
        final var messageDTO = mapper.convertValue(
                message.getPayload(),
                WsGroupMessageDeleteDTO.class
        );
        final var userId = UUID.fromString((String) session.getAttributes().get("userId"));
        final var pair = deleteMessage(messageDTO, userId);
        final var savedMessageDTO = GroupMessageRetrievalDTO.fromGroupMessage(pair.getSecond());
        final var webSocketMessage = new ChatWebSocketMessage(WebSocketMessageType.GROUP_MESSAGE_DELETE, savedMessageDTO);
        final var binaryMessage = WebSocketMessageMapper.webSocketMessageToBinaryMessage(webSocketMessage);
        webSocketHandler.sendToUsersWhoseIdIn(pair.getFirst(), binaryMessage);
    }

    @Transactional
    public Pair<List<String>, GroupMessage> deleteMessage(
            WsGroupMessageDeleteDTO messageDTO,
            UUID userId
    ) {
        final var authorizedUser = userService.findUserById(userId);
        final var groupChat = groupChatService.getGroupChatById(messageDTO.getChatId(), authorizedUser.getId());
        final var message = GroupMessage.builder()
                .id(messageDTO.getMessageId())
                .author(authorizedUser)
                .chat(groupChat)
                .build();

        groupChatMessageService.deleteMessage(groupChat, message, authorizedUser);
        final var usersIds = getGroupChatMembersIds(groupChat, authorizedUser);
        return new Pair(usersIds, message);
    }

    @Override
    @Transactional
    public void handleReadRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<Object> message
    ) {}
}
