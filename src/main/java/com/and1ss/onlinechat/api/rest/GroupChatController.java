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

@Transactional
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
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);

        return groupChatService.getAllGroupChatsForUserDTO(authorizedUser);
    }


    @GetMapping("/{chat_id}")
    public GroupChatRetrievalDTO getGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        return formGroupChatRetrievalDTOForAuthorizedUser(groupChat, authorizedUser);
    }

    @PostMapping
    public GroupChatRetrievalDTO createGroupChat(
            @RequestBody GroupChatCreationDTO chatCreationDTO,
            @RequestHeader("Authorization") String token
    ) {
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

        GroupChat createdChat = groupChatService
                .createGroupChat(toBeCreated, participants, authorizedUser);

        return formGroupChatRetrievalDTOForAuthorizedUser(createdChat, authorizedUser);
    }

    @PatchMapping("/{chat_id}")
    public void patchGroupChat(
            @RequestBody GroupChatPatchDTO chatPatchDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
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
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        List<GroupMessage> messages = groupChatMessageService
                .getAllMessages(groupChat, authorizedUser);

        return messages.stream()
                .map(GroupMessageRetrievalDTO::fromGroupMessage)
                .collect(Collectors.toList());
    }

    @PostMapping("/{chat_id}/messages")
    public GroupMessageRetrievalDTO addMessageToGroupChat(
            @RequestBody GroupMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        GroupMessage message = GroupMessage.builder()
                .author(authorizedUser)
                .chat(groupChat)
                .contents(messageCreationDTO.getContents())
                .build();

        GroupMessage savedMessage = groupChatMessageService
                .addMessage(groupChat, message, authorizedUser);

        return GroupMessageRetrievalDTO.fromGroupMessage(savedMessage);
    }

    @PatchMapping("/{chat_id}/messages/{message_id}")
    public void patchMessageOfGroupChat(
            @RequestBody GroupMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        GroupMessage message = GroupMessage.builder()
                .id(messageId)
                .author(authorizedUser)
                .chat(groupChat)
                .contents(messageCreationDTO.getContents())
                .build();

         groupChatMessageService
                .patchMessage(groupChat, message, authorizedUser);
    }

    @DeleteMapping("/{chat_id}/messages/{message_id}")
    public void deleteMessageOfGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        GroupChat groupChat = groupChatService.getGroupChatById(chatId, authorizedUser);
        GroupMessage message = groupChatMessageService.getMessageById(messageId);

        groupChatMessageService
                .deleteMessage(groupChat, message, authorizedUser);
    }


    private GroupChatRetrievalDTO
    formGroupChatRetrievalDTOForAuthorizedUser(
            GroupChat groupChat,
            AccountInfo author
    ) {
        List<AccountInfo> participants =
                groupChatService.getGroupChatMembers(groupChat, author);
        var lastMessage = groupChatMessageService.getLastMessage(groupChat, author);

        GroupMessageRetrievalDTO lastMessageDTO = null;
        if (lastMessage != null) {
            lastMessageDTO = GroupMessageRetrievalDTO.fromGroupMessage(lastMessage);
        }
        return GroupChatRetrievalDTO.fromGroupChat(groupChat, participants, lastMessageDTO);
    }
}
