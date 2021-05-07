package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.domain.AccessToken;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.services.dto.LoginInfoDTO;
import com.and1ss.onlinechat.services.dto.RegisterInfoDTO;
import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    AccountInfo registerUser(RegisterInfoDTO registerInfo);

    AccessToken loginUser(LoginInfoDTO credentials);

    AccountInfo authorizeUserByAccessToken(String accessToken);

    AccountInfo authorizeUserByBearerToken(String accessToken);

    AccountInfo findUserByLogin(String login);

    AccountInfo findUserById(UUID id);

    List<AccountInfo> findUsersByListOfIds(List<UUID> ids);

    List<AccountInfoRetrievalDTO> findUsersWhoAreNotCurrentUserFriends(String accessToken);

    List<AccountInfoRetrievalDTO> findUsersWhoAreNotCurrentUserFriendsAndLoginLike(String accessToken,
                                                                                   String loginLike);
}
