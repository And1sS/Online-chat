package com.and1ss.onlinechat.services.user.controllers;

import com.and1ss.onlinechat.services.user.repos.AccessTokenRepository;
import com.and1ss.onlinechat.services.user.repos.AccountInfoRepository;
import com.and1ss.onlinechat.services.user.model.AccessToken;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthenticationController {

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @PostMapping("/register")
    private AccountInfo registerUser(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("login") String login,
            @Param("password") String password) {
        AccountInfo accountInfo = AccountInfo.builder()
                .name(name)
                .surname(surname)
                .login(login)
                .passwordHash(password)
                .build();

        try {
            return accountInfoRepository.save(accountInfo);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    @GetMapping("auth/users")
    private List<AccountInfo> findAllUsers() {
        return accountInfoRepository.findAll();
    }

    @GetMapping("auth/tokens")
    private List<AccessToken> findAllTokens() {
        return accessTokenRepository.findAll();
    }
}
