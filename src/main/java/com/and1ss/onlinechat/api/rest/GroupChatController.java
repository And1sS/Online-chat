package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.api.dto.*;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.domain.AccountInfo;
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

    @GetMapping("/all")
    public List<GroupChatRetrievalDTO>
    getAllGroupChats(@RequestHeader("Authorization") String token) {
        return getAllGroupChatsTransaction(token);
    }

    @Transactional
    public List<GroupChatRetrievalDTO> getAllGroupChatsTransaction(String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);

        return groupChatService.getAllGroupChatsDTOForUser(authorizedUser);
    }

    @GetMapping("/{chat_id}")
    public GroupChatRetrievalDTO getGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        return getGroupChatDTOTransaction(chatId, token);
    }

    @Transactional
    public GroupChatRetrievalDTO getGroupChatDTOTransaction(UUID chatId, String token) {
        final AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        return groupChatService.getGroupChatDTOById(chatId, authorizedUser);
    }

    @PostMapping
    public GroupChatRetrievalDTO createGroupChat(
            @RequestBody GroupChatCreationDTO chatCreationDTO,
            @RequestHeader("Authorization") String token
    ) {
        final GroupChat createdChat = createGroupChatTransaction(chatCreationDTO, token);
        return GroupChatRetrievalDTO.fromGroupChat(createdChat, null);
    }

    @Transactional
    public GroupChat createGroupChatTransaction(GroupChatCreationDTO chatCreationDTO, String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        List<UUID> participantsIds = chatCreationDTO.getParticipants();

        List<AccountInfo> participants =
                userService.findUsersByListOfIds(participantsIds);

        if (participants.size() < 2) {
            throw new BadRequestException("Chats must have at least two members");
        }

        GroupChat toBeCreated = GroupChat.builder()
                .title(chatCreationDTO.getTitle())
                .about(chatCreationDTO.getAbout())
                .creator(authorizedUser)
                .build();

        return groupChatService.createGroupChat(toBeCreated, participants, authorizedUser);
    }

    @PatchMapping("/{chat_id}")
    public void patchGroupChat(
            @RequestBody GroupChatPatchDTO chatPatchDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        patchGroupChatTransaction(chatPatchDTO, chatId, token);
    }

    @Transactional
    public void patchGroupChatTransaction(GroupChatPatchDTO chatPatchDTO, UUID chatId, String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService
                .getGroupChatById(chatId, authorizedUser);

        if (chatPatchDTO.getAbout() != null && !chatPatchDTO.getAbout().isEmpty()) {
            groupChat.setAbout(chatPatchDTO.getAbout());
        }

        if (chatPatchDTO.getTitle() != null && !chatPatchDTO.getTitle().isEmpty()) {
            groupChat.setTitle(chatPatchDTO.getTitle());
        }

        groupChatService.patchGroupChat(groupChat, authorizedUser);
    }

    @PostMapping("/{chat_id}/users")
    public void addUserToGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token,
            @RequestBody UUID userId
    ) {
        addUserToGroupChatTransaction(chatId, userId, token);
    }

    @Transactional
    public void addUserToGroupChatTransaction(UUID chatId, UUID userId, String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        AccountInfo toBeAddedUser = userService.findUserById(userId);
        groupChatService.addUser(groupChat, authorizedUser, toBeAddedUser);
    }

    @DeleteMapping("/{chat_id}/users/{user_id}")
    public void deleteUserFromGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("user_id") UUID userId,
            @RequestHeader("Authorization") String token
    ) {
        deleteUserFromGroupChatTransaction(chatId, userId, token);
    }

    @Transactional
    public void deleteUserFromGroupChatTransaction(UUID chatId, UUID userId, String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        AccountInfo toBeDeletedUser = userService.findUserById(userId);
        groupChatService.deleteUser(groupChat, authorizedUser, toBeDeletedUser);
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
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        return groupChatMessageService.getAllMessages(groupChat, authorizedUser);
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
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
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
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);

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
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        GroupMessage message = groupChatMessageService.getMessageById(messageId);

        groupChatMessageService
                .deleteMessage(groupChat, message, authorizedUser);
    }
}
