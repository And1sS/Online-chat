package com.and1ss.onlinechat.services.user;

import com.and1ss.onlinechat.services.user.model.LoginInfo;
import com.and1ss.onlinechat.services.user.model.AccessToken;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.and1ss.onlinechat.services.user.model.RegisterInfo;

public interface UserService {
        AccountInfo registerUser(RegisterInfo registerInfo);
        AccessToken loginUser(LoginInfo credentials);
        AccountInfo authorizeUserByAccessToken(String accessToken);
        AccountInfo findUserByLogin(String login);
}
