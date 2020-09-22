package com.and1ss.onlinechat.services.user.controllers;

import com.and1ss.onlinechat.services.user.dto.AccessTokenDTO;
import com.and1ss.onlinechat.services.user.dto.LoginInfoDTO;
import com.and1ss.onlinechat.services.user.errors.InvalidLoginCredentialsException;
import com.and1ss.onlinechat.services.user.errors.InvalidRegisterDataException;
import com.and1ss.onlinechat.services.user.repos.AccessTokenRepository;
import com.and1ss.onlinechat.services.user.repos.AccountInfoRepository;
import com.and1ss.onlinechat.services.user.model.AccessToken;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    // TODO: add password hashing
    @PostMapping("/register")
    private AccountInfo registerUser(@RequestBody AccountInfo registerInfo) {
        try {
            return accountInfoRepository.save(registerInfo);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRegisterDataException("Login already present");
        }
    }

    @GetMapping("/login")
    private AccessTokenDTO loginUser(@RequestBody LoginInfoDTO loginInfo) {
        System.out.println(loginInfo);
        AccountInfo userInfo =
                accountInfoRepository.findAccountInfoByLogin(loginInfo.getLogin());

        if (userInfo == null || !userInfo.getPasswordHash().equals(loginInfo.getPassword())) {
            throw new InvalidLoginCredentialsException();
        }

        AccessToken accessToken = AccessToken.builder()
                .userId(userInfo.getId())
                .build();

        AccessToken saved = accessTokenRepository.save(accessToken);
        return new AccessTokenDTO(saved.getUserId(), saved.getToken());
    }

    @GetMapping("/users")
    private List<AccountInfo> findAllUsers() {
        return accountInfoRepository.findAll();
    }

    @GetMapping("/tokens")
    private List<AccessToken> findAllTokens() {
        return accessTokenRepository.findAll();
    }
}
