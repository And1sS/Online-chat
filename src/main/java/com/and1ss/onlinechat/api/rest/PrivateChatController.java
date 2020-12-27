package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.services.PrivateChatMessageService;
import com.and1ss.onlinechat.services.PrivateChatService;
import com.and1ss.onlinechat.api.dto.PrivateChatCreationDTO;
import com.and1ss.onlinechat.api.dto.PrivateChatRetrievalDTO;
import com.and1ss.onlinechat.api.dto.PrivateMessageCreationDTO;
import com.and1ss.onlinechat.api.dto.PrivateMessageRetrievalDTO;
import com.and1ss.onlinechat.domain.PrivateChat;
import com.and1ss.onlinechat.domain.PrivateMessage;
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
@RequestMapping("api/private-chat-service/chats")
public class PrivateChatController {

    PrivateChatService privateChatService;

    PrivateChatMessageService privateChatMessageService;

    UserService userService;

    @Autowired
    public PrivateChatController(
            PrivateChatService privateChatService,
            PrivateChatMessageService privateChatMessageService,
            UserService userService
    ) {
        this.privateChatService = privateChatService;
        this.privateChatMessageService = privateChatMessageService;
        this.userService = userService;
    }

    @GetMapping
    public List<PrivateChatRetrievalDTO> getAllPrivateChats(
            @RequestHeader("Authorization") String token
    ) {
        return getAllPrivateChatsTransaction(token);
    }

    @Transactional
    public List<PrivateChatRetrievalDTO> getAllPrivateChatsTransaction(String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        return privateChatService
                .getAllPrivateChatsWithLastMessageDTOForUser(authorizedUser);
    }

    @GetMapping("/{chat_id}")
    public PrivateChatRetrievalDTO getPrivateChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        return getPrivateChatTransaction(chatId, token);
    }

    @Transactional
    public PrivateChatRetrievalDTO getPrivateChatTransaction(UUID chatId, String token) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        return privateChatService
                .getPrivateChatWithLastMessageDTOById(chatId, authorizedUser);
    }

    @PostMapping
    public PrivateChatRetrievalDTO createPrivateChat(
            @RequestBody PrivateChatCreationDTO chatCreationDTO,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
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

        return PrivateChatRetrievalDTO.fromPrivateChat(createdChat, null);
    }

    @PostMapping("/{chat_id}/messages")
    public PrivateMessageRetrievalDTO addMessageToPrivateChat(
            @RequestBody PrivateMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);

        PrivateMessage message = PrivateMessage.builder()
                .author(authorizedUser)
                .chat(privateChat)
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
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        List<PrivateMessage> messages = privateChatMessageService
                .getAllMessages(privateChat, authorizedUser);

        return messages.stream()
                .map(PrivateMessageRetrievalDTO::fromPrivateMessage)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{chat_id}/messages/{message_id}")
    public PrivateMessageRetrievalDTO patchMessageOfPrivateChat(
            @RequestBody PrivateMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);

        PrivateMessage message = PrivateMessage.builder()
                .id(messageId)
                .author(authorizedUser)
                .chat(privateChat)
                .contents(messageCreationDTO.getContents())
                .build();

        PrivateMessage savedMessage = privateChatMessageService
                .patchMessage(privateChat, message, authorizedUser);

        return PrivateMessageRetrievalDTO.fromPrivateMessage(savedMessage);
    }

    @DeleteMapping("/{chat_id}/messages/{message_id}")
    public void deleteMessageOfPrivateChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = userService.authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        PrivateMessage message = privateChatMessageService.getMessageById(messageId);

        privateChatMessageService.deleteMessage(privateChat, message, authorizedUser);
    }
}