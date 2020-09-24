package com.and1ss.onlinechat.services.group_chat.api;

import com.and1ss.onlinechat.services.group_chat.GroupChatService;
import com.and1ss.onlinechat.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chats/group")
public class GroupChatController {
    @Autowired
    GroupChatService groupChatService;

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public void getAllGroupChats(@RequestHeader("Authorization") String token) {
    }

    @GetMapping("/{id}")
    public void getGroupChat(
            @PathVariable("id") UUID id,
            @RequestHeader("Authorization") String token
    ) { }

    @PostMapping
    public void createGroupChat(@RequestHeader("Authorization") String token) {
    }

    @PatchMapping("/{id}")
    public void patchGroupChat(
            @PathVariable("id") UUID id,
            @RequestHeader("Authorization") String token
    ) { }

    @PostMapping("/{id}/")
    public void addUserToGroupChat(
            @PathVariable("id") UUID id,
            @RequestHeader("Authorization") String token
    ) {}

    @DeleteMapping("/{id}/{user_id}")
    public void deleteUserFromGroupChat(
            @PathVariable("id") UUID id,
            @PathVariable("user_id") UUID userId,
            @RequestHeader("Authorization") String token
    ) {}
}
