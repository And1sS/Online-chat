package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.api.dto.*;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group-chat-service/chats")
public class GroupChatController {

    private final GroupChatService groupChatService;

    private final GroupChatMessageService groupChatMessageService;

    private final UserService userService;

    @Autowired
    public GroupChatController(
            GroupChatService groupChatService,
            GroupChatMessageService groupChatMessageService,
            UserService userService
    ) {
        this.groupChatService = groupChatService;
        this.groupChatMessageService = groupChatMessageService;
        this.userService = userService;
    }

    @GetMapping
    public List<GroupChatRetrievalDTO>
    getAllGroupChats(@RequestHeader("Authorization") String token) {
        return getAllGroupChatsTransaction(token);
    }

    @Transactional
    public List<GroupChatRetrievalDTO> getAllGroupChatsTransaction(String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);

        return groupChatService.getAllGroupChatsForUser(authorizedUser.getId());
    }

    @GetMapping("/{chat_id}")
    public GroupChatRetrievalDTO getGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        final AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        return groupChatService.getGroupChatById(chatId, authorizedUser.getId());
    }


    @PostMapping
    public GroupChatRetrievalDTO createGroupChat(
            @RequestBody GroupChatCreationDTO chatCreationDTO,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        return groupChatService.createGroupChat(chatCreationDTO, authorizedUser.getId());
    }

    @PatchMapping("/{chat_id}")
    public void patchGroupChat(
            @RequestBody GroupChatPatchDTO chatPatchDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        groupChatService.patchGroupChat(chatId, chatPatchDTO, authorizedUser.getId());
    }

    @PostMapping("/{chat_id}/users")
    public void addUserToGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token,
            @RequestBody UUID userId
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        groupChatService.addUser(chatId, userId, authorizedUser.getId());
    }

    @DeleteMapping("/{chat_id}/users/{user_id}")
    public void deleteUserFromGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("user_id") UUID userId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        groupChatService.deleteUser(chatId, userId, authorizedUser.getId());
    }


    @GetMapping("/{chat_id}/messages")
    public List<GroupMessageRetrievalDTO> getGroupChatMessages(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        List<GroupMessage> messages = getGroupChatMessagesTransaction(chatId, token);
        return messages.stream()
                .map(GroupMessageRetrievalDTO::fromGroupMessage)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupMessage> getGroupChatMessagesTransaction(UUID chatId, String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        return groupChatMessageService.getAllMessages(chatId, authorizedUser.getId());
    }

    @PostMapping("/{chat_id}/messages")
    public GroupMessageRetrievalDTO addMessageToGroupChat(
            @RequestBody GroupMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {

        GroupMessage savedMessage = addMessageToGroupChatTransaction(messageCreationDTO, chatId, token);
        return GroupMessageRetrievalDTO.fromGroupMessage(savedMessage);
    }

    @Transactional
    public GroupMessage addMessageToGroupChatTransaction(
            GroupMessageCreationDTO messageCreationDTO,
            UUID chatId, String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser.getId());
        GroupMessage message = GroupMessage.builder()
                .author(authorizedUser)
                .chat(groupChat)
                .contents(messageCreationDTO.getContents())
                .build();

        return groupChatMessageService
                .addMessage(groupChat, message, authorizedUser);
    }

    @PatchMapping("/{chat_id}/messages/{message_id}")
    public void patchMessageOfGroupChat(
            @RequestBody GroupMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        patchMessageOfGroupChatTransaction(token, chatId, messageId, messageCreationDTO);
    }

    @Transactional
    public GroupMessage patchMessageOfGroupChatTransaction(
            String token, UUID chatId,
            UUID messageId, GroupMessageCreationDTO messageCreationDTO
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser.getId());

        GroupMessage message = GroupMessage.builder()
                .id(messageId)
                .author(authorizedUser)
                .chat(groupChat)
                .contents(messageCreationDTO.getContents())
                .build();

        return groupChatMessageService
                .patchMessage(groupChat, message, authorizedUser);
    }

    @DeleteMapping("/{chat_id}/messages/{message_id}")
    public void deleteMessageOfGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        deleteMessageOfGroupChatTransaction(chatId, messageId, token);
    }

    @Transactional
    public void deleteMessageOfGroupChatTransaction(
            UUID chatId, UUID messageId, String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser.getId());
        GroupMessage message = groupChatMessageService.getMessageById(messageId);

        groupChatMessageService
                .deleteMessage(groupChat, message, authorizedUser);
    }
}
