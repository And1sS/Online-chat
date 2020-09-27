package com.and1ss.onlinechat.services.private_chat.api;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.services.group_chat.api.dto.GroupChatRetrievalDTO;
import com.and1ss.onlinechat.services.group_chat.api.dto.GroupMessageCreationDTO;
import com.and1ss.onlinechat.services.group_chat.api.dto.GroupMessageRetrievalDTO;
import com.and1ss.onlinechat.services.group_chat.model.GroupChat;
import com.and1ss.onlinechat.services.group_chat.model.GroupMessage;
import com.and1ss.onlinechat.services.private_chat.PrivateChatMessageService;
import com.and1ss.onlinechat.services.private_chat.PrivateChatService;
import com.and1ss.onlinechat.services.private_chat.api.dto.PrivateChatCreationDTO;
import com.and1ss.onlinechat.services.private_chat.api.dto.PrivateChatRetrievalDTO;
import com.and1ss.onlinechat.services.private_chat.api.dto.PrivateMessageCreationDTO;
import com.and1ss.onlinechat.services.private_chat.api.dto.PrivateMessageRetrievalDTO;
import com.and1ss.onlinechat.services.private_chat.model.PrivateChat;
import com.and1ss.onlinechat.services.private_chat.model.PrivateMessage;
import com.and1ss.onlinechat.services.user.UserService;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chats/private")
public class PrivateChatController {
    @Autowired
    PrivateChatService privateChatService;

    @Autowired
    PrivateChatMessageService privateChatMessageService;

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public List<PrivateChatRetrievalDTO> getAllPrivateChats(@RequestHeader("Authorization") String token) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);

        List<PrivateChat> privateChats =
                privateChatService.getAllPrivateChatsForUser(authorizedUser);
        return privateChats.stream()
                .map(PrivateChatRetrievalDTO::fromPrivateChat)
                .collect(Collectors.toList());
    }

    @GetMapping("/{chat_id}")
    public PrivateChatRetrievalDTO getPrivateChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        return PrivateChatRetrievalDTO.fromPrivateChat(privateChat);
    }

    @PostMapping
    public PrivateChatRetrievalDTO createPrivateChat(
            @RequestBody PrivateChatCreationDTO chatCreationDTO,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        AccountInfo user2 = userService.findUserById(chatCreationDTO.getUserId());

        if (user2.equals(authorizedUser)) {
            throw new BadRequestException("User can not create private chats with himself");
        }

        PrivateChat toBeCreated = PrivateChat.builder()
                .user1(authorizedUser)
                .user2(user2)
                .build();

        PrivateChat createdChat = privateChatService
                .createPrivateChat(toBeCreated, authorizedUser);

        return PrivateChatRetrievalDTO.fromPrivateChat(createdChat);
    }

    @PostMapping("/{chat_id}/messages")
    public PrivateMessageRetrievalDTO addMessageToPrivateChat(
            @RequestBody PrivateMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);

        PrivateMessage message = PrivateMessage.builder()
                .author(authorizedUser)
                .chatId(chatId)
                .contents(messageCreationDTO.getContents())
                .build();

        PrivateMessage savedMessage = privateChatMessageService
                .addMessage(privateChat, message, authorizedUser);

        return PrivateMessageRetrievalDTO.fromPrivateMessage(savedMessage);
    }


    @GetMapping("/{chat_id}/messages")
    public List<PrivateMessageRetrievalDTO> getPrivateChatMessages(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        List<PrivateMessage> messages = privateChatMessageService
                .getAllMessages(privateChat, authorizedUser);

        return messages.stream()
                .map(PrivateMessageRetrievalDTO::fromPrivateMessage)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{chat_id}/messages/{message_id}")
    public PrivateMessageRetrievalDTO patchMessageOfGroupChat(
            @RequestBody GroupMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);

        PrivateMessage message = PrivateMessage.builder()
                .id(messageId)
                .author(authorizedUser)
                .chatId(chatId)
                .contents(messageCreationDTO.getContents())
                .build();

        PrivateMessage savedMessage = privateChatMessageService
                .patchMessage(privateChat, message, authorizedUser);

        return PrivateMessageRetrievalDTO.fromPrivateMessage(savedMessage);
    }

    @DeleteMapping("/{chat_id}/messages/{message_id}")
    public void patchMessageOfGroupChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        PrivateMessage message = privateChatMessageService.getMessageById(messageId);

        privateChatMessageService.deleteMessage(privateChat, message, authorizedUser);
    }

    private AccountInfo authorizeUserByBearerToken(String token) {
        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
        return userService.authorizeUserByAccessToken(parsedAccessToken);
    }
}