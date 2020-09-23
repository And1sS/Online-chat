package com.and1ss.onlinechat.services.chat.api.controllers;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.services.chat.ChatService;
import com.and1ss.onlinechat.services.chat.api.dto.ChatDTO;
import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChat;
import com.and1ss.onlinechat.services.chat.model.private_chat.PrivateChat;
import com.and1ss.onlinechat.services.chat.repos.PrivateChatRepository;
import com.and1ss.onlinechat.services.user.UserService;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    UserService userService;

    @Autowired
    ChatService chatService;

    //TODO: Rewrite this to return ChatDTO object
    @PostMapping("/create")
    public void createChat(@RequestBody ChatDTO chat, @RequestHeader("Authorization") String token) {
        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
        AccountInfo author = userService.authorizeUserByAccessToken(parsedAccessToken);

        if (chat.getUsers().size() < 2) {
            throw new BadRequestException("Chats must have at least two members");
        }

        switch (chat.getType()) {
            case ChatDTO.CHAT_PRIVATE: {
                handleCreatePrivateChat(chat, author);
                break;
            }
            case ChatDTO.CHAT_GROUP: {
                handleCreateGroupChat(chat, author);
                break;
            }
            default:
                throw new BadRequestException("Chats must have either private or group type");
        }
    }

    private PrivateChat handleCreatePrivateChat(ChatDTO chat, AccountInfo author) {
        List<UUID> usersIds = chat.getUsers();
        if (usersIds.size() > 2) {
            throw new BadRequestException("Private chats can only have two members");
        }
        AccountInfo user1 = userService.findUserById(usersIds.get(0));
        AccountInfo user2 = userService.findUserById(usersIds.get(1));

        if (user1.equals(user2)) {
            throw new BadRequestException("Private chat cannot have two equal members");
        }

        return chatService.createPrivateChat(new PrivateChat(user1, user2), author);
    }

    private GroupChat handleCreateGroupChat(ChatDTO chat, AccountInfo author) {
        List<UUID> usersIds = chat.getUsers();
        List<AccountInfo> participants = userService.findUsersByListOfIds(usersIds);

        GroupChat toBeCreated = GroupChat.builder()
                .title(chat.getTitle())
                .about(chat.getAbout())
                .creator(author)
                .build();

        return chatService.createGroupChat(toBeCreated, participants, author);
    }

    @Autowired
    PrivateChatRepository privateChatRepository;

    @GetMapping("/{id}")
    public PrivateChat getPrivateChatById(@PathVariable("id") UUID id) {
        //return privateChatRepository.findPrivateChatById(id);
        return null;
    }

    @GetMapping
    public List<PrivateChat> getAllPrivateChats() {
        return privateChatRepository.findAll();
    }

}
