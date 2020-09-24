package com.and1ss.onlinechat.services.chat.api.controllers;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.services.group_chat.model.GroupChat;
import com.and1ss.onlinechat.services.private_chat.model.PrivateChat;
import com.and1ss.onlinechat.services.user.UserService;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
public class ChatController {
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    ChatService chatService;
//
//    @PostMapping
//    public void createChat(@RequestBody ChatFullDTO chat, @RequestHeader("Authorization") String token) {
//        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
//        AccountInfo author = userService.authorizeUserByAccessToken(parsedAccessToken);
//
//        if (chat.getUsers().size() < 2) {
//            throw new BadRequestException("Chats must have at least two members");
//        }
//
//        if (chat.getType().equals(ChatType.CHAT_PRIVATE)) {
//            handleCreatePrivateChat(chat, author);
//        } else if (chat.getType().equals(ChatType.CHAT_GROUP)) {
//            handleCreateGroupChat(chat, author);
//        } else {
//            throw new BadRequestException("Chats must have either private or group type");
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ChatFullDTO getChatById(
//            @PathVariable("id") UUID id,
//            @RequestHeader("Authorization") String token
//    ) {
//        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
//        AccountInfo author = userService.authorizeUserByAccessToken(parsedAccessToken);
//
//        try {
//            return new ChatFullDTO(chatService.getPrivateChatById(id, author));
//        } catch (Exception e) {}
//
//        try {
//            GroupChat groupChat = chatService.getGroupChatById(id, author);
//            List<AccountInfo> participants = chatService.getGroupChatMembers(groupChat, author);
//            return new ChatFullDTO(groupChat, participants);
//        } catch (Exception e) {}
//
//        throw new BadRequestException("Invalid chat id");
//    }
//
//    private PrivateChat handleCreatePrivateChat(ChatFullDTO chat, AccountInfo author) {
//        List<UUID> usersIds = chat.getUsers();
//        if (usersIds.size() > 2) {
//            throw new BadRequestException("Private chats can only have two members");
//        }
//        AccountInfo user1 = userService.findUserById(usersIds.get(0));
//        AccountInfo user2 = userService.findUserById(usersIds.get(1));
//
//        if (user1.equals(user2)) {
//            throw new BadRequestException("Private chat cannot have two equal members");
//        }
//
//        return chatService.createPrivateChat(new PrivateChat(user1, user2), author);
//    }
//
//    private GroupChat handleCreateGroupChat(ChatFullDTO chat, AccountInfo author) {
//        List<UUID> usersIds = chat.getUsers();
//        List<AccountInfo> participants = userService.findUsersByListOfIds(usersIds);
//
//        GroupChat toBeCreated = GroupChat.builder()
//                .title(chat.getTitle())
//                .about(chat.getAbout())
//                .creator(author)
//                .build();
//
//        return chatService.createGroupChat(toBeCreated, participants, author);
//    }
//
//    @GetMapping("/all")
//    public List<ChatPartialDTO> getAllChats(@RequestHeader("Authorization") String token) {
//        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
//        AccountInfo user = userService.authorizeUserByAccessToken(parsedAccessToken);
//
//        List<ChatPartialDTO> allChats = new ArrayList();
//        allChats.addAll(getAllGroupChatsDTO(user));
//        allChats.addAll(getAllPrivateChatsDTO(user));
//
//        return allChats;
//    }
//
//    private List<ChatPartialDTO> getAllPrivateChatsDTO(AccountInfo user) {
//        return chatService.getAllPrivateChatsForUser(user)
//                .stream()
//                .map(privateChat ->
//                        new ChatPartialDTO(
//                                ChatType.CHAT_PRIVATE,
//                                privateChat.getId()
//                        )
//                ).collect(Collectors.toList());
//    }
//
//    private List<ChatPartialDTO> getAllGroupChatsDTO(AccountInfo user) {
//        return chatService.getAllGroupChatsForUser(user)
//                .stream()
//                .map(groupChat ->
//                        new ChatPartialDTO(
//                                ChatType.CHAT_GROUP,
//                                groupChat.getId()
//                        )
//                ).collect(Collectors.toList());
//    }
}
