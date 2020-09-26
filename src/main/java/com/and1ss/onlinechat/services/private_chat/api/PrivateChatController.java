package com.and1ss.onlinechat.services.private_chat.api;

import com.and1ss.onlinechat.services.private_chat.PrivateChatService;
import com.and1ss.onlinechat.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chats/private")
public class PrivateChatController {
    @Autowired
    PrivateChatService privateChatService;

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public void getAllPrivateChats(@RequestHeader("Authorization") String token) { }

    @GetMapping("/{id}")
    public void getPrivateChat(
            @PathVariable("id") UUID id,
            @RequestHeader("Authorization") String token
    ) { }

    @PostMapping
    public void createPrivateChat(@RequestHeader("Authorization") String token) { }

}