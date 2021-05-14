package com.and1ss.onlinechat.api.ws.handlers;

import com.and1ss.onlinechat.api.ws.WebSocketUtil;
import com.and1ss.onlinechat.api.ws.base.AbstractWebSocketHandler;
import com.and1ss.onlinechat.api.ws.base.CrudRequestHandler;
import com.and1ss.onlinechat.api.ws.base.WebSocketMessageMapper;
import com.and1ss.onlinechat.api.ws.base.WebSocketMessageType;
import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.and1ss.onlinechat.api.ws.dto.WsGroupMessageDeleteDTO;
import com.and1ss.onlinechat.api.ws.dto.WsGroupMessagePatchDTO;
import com.and1ss.onlinechat.api.ws.mappers.WsGroupMessageMapper;
import com.and1ss.onlinechat.services.dto.GroupMessageCreationDTO;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.services.dto.GroupMessagePatchDTO;
import com.and1ss.onlinechat.services.dto.GroupMessageRetrievalDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class GroupMessageRequestHandler implements CrudRequestHandler {

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
        WsGroupMessagePatchDTO messageDTO = mapper.convertValue(
                message.getPayload(),
                WsGroupMessagePatchDTO.class
        );

        UUID userId = WebSocketUtil.getUserIdFromSession(session);
        UUID messageId = messageDTO.getMessageId();
        UUID chatId = messageDTO.getChatId();
        GroupMessagePatchDTO patchDTO = WsGroupMessageMapper.toGroupMessagePatchDTO(messageDTO);

        GroupMessageRetrievalDTO resultDTO = groupChatMessageService
                .patchMessage(messageId, patchDTO, userId);
        List<UUID> usersIds = groupChatService.getGroupChatMembersIds(chatId, userId);

        ChatWebSocketMessage webSocketMessage = new ChatWebSocketMessage(
                WebSocketMessageType.GROUP_MESSAGE_PATCH, resultDTO);
        BinaryMessage binaryMessage = WebSocketMessageMapper
                .webSocketMessageToBinaryMessage(webSocketMessage);
        webSocketHandler.sendToUsersWhoseIdIn(usersIds, binaryMessage);
    }

    @Override
    public void handleDeleteRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) throws JsonProcessingException {
        WsGroupMessageDeleteDTO messageDTO = mapper.convertValue(
                message.getPayload(),
                WsGroupMessageDeleteDTO.class
        );
        UUID userId = WebSocketUtil.getUserIdFromSession(session);
        UUID chatId = messageDTO.getChatId();
        UUID messageId = messageDTO.getMessageId();

        groupChatMessageService.deleteMessage(messageId, userId);
        List<UUID> usersIds = groupChatService.getGroupChatMembersIds(chatId, userId);
        BinaryMessage binaryMessage = WebSocketMessageMapper
                .webSocketMessageToBinaryMessage(message);
        webSocketHandler.sendToUsersWhoseIdIn(usersIds, binaryMessage);
    }

    @Override
    public void handleReadRequest(
            WebSocketSession session,
            AbstractWebSocketHandler webSocketHandler,
            ChatWebSocketMessage<?> message
    ) {}
}
