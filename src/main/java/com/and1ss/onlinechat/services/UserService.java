package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.domain.LoginInfo;
import com.and1ss.onlinechat.domain.AccessToken;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.RegisterInfo;

import java.util.List;
import java.util.UUID;

public interface UserService {
        AccountInfo registerUser(RegisterInfo registerInfo);
        AccessToken loginUser(LoginInfo credentials);
        AccountInfo authorizeUserByAccessToken(String accessToken);
        AccountInfo authorizeUserByBearerToken(String accessToken);
        AccountInfo findUserByLogin(String login);
        AccountInfo findUserById(UUID id);
        List<AccountInfo> findUsersByListOfIds(List<UUID> ids);
}
