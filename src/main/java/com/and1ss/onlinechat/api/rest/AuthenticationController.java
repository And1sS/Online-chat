package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.services.dto.AccessTokenRetrievalDTO;
import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.dto.LoginInfoDTO;
import com.and1ss.onlinechat.services.dto.RegisterInfoDTO;
import com.and1ss.onlinechat.services.mappers.AccountInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-service/auth")
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService service) {
        userService = service;
    }

    @PostMapping("/register")
    private AccountInfoRetrievalDTO
    registerUser(@RequestBody RegisterInfoDTO registerInfo) {
        return AccountInfoMapper.toAccountInfoRetrievalDTO(userService.registerUser(registerInfo));
    }

    @PutMapping("/login")
    private AccessTokenRetrievalDTO loginUser(@RequestBody LoginInfoDTO credentials) {
        return new AccessTokenRetrievalDTO(userService.loginUser(credentials));
    }
}
