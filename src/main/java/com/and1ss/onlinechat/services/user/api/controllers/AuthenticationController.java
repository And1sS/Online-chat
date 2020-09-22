package com.and1ss.onlinechat.services.user.api.controllers;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.user.UserService;
import com.and1ss.onlinechat.services.user.api.dto.AccessTokenDTO;
import com.and1ss.onlinechat.services.user.api.dto.AccountInfoDTO;
import com.and1ss.onlinechat.services.user.model.LoginInfo;
import com.and1ss.onlinechat.services.user.model.RegisterInfo;
import com.and1ss.onlinechat.services.user.model.AccessToken;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.and1ss.onlinechat.services.user.repos.AccessTokenRepository;
import com.and1ss.onlinechat.services.user.repos.AccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private AccountInfoDTO registerUser(@RequestBody RegisterInfo registerInfo) {
        return new AccountInfoDTO(userService.registerUser(registerInfo));
    }

    @GetMapping("/login")
    private AccessTokenDTO loginUser(@RequestBody LoginInfo credentials) {
        return new AccessTokenDTO(userService.loginUser(credentials));
    }

    @GetMapping("/test")
    private AccountInfoDTO authorizeUser(@RequestHeader("Authorization") String token) {
        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
        return new AccountInfoDTO(userService.authorizeUserByAccessToken(parsedAccessToken));
    }

    // TODO: delete this on service finish
    @GetMapping("/users")
    private List<AccountInfo> findAllUsers() {
        return accountInfoRepository.findAll();
    }

    @GetMapping("/tokens")
    private List<AccessToken> findAllTokens() {
        return accessTokenRepository.findAll();
    }
}
