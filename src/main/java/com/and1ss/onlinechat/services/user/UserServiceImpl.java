package com.and1ss.onlinechat.services.user;

import com.and1ss.onlinechat.exceptions.*;
import com.and1ss.onlinechat.services.user.model.AccessToken;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.and1ss.onlinechat.services.user.model.LoginInfo;
import com.and1ss.onlinechat.services.user.model.RegisterInfo;
import com.and1ss.onlinechat.services.user.password_hasher.PasswordHasher;
import com.and1ss.onlinechat.services.user.repos.AccessTokenRepository;
import com.and1ss.onlinechat.services.user.repos.AccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Override
    public AccountInfo registerUser(RegisterInfo registerInfo) {
        try {
            return accountInfoRepository.save(new AccountInfo(registerInfo, passwordHasher));
        } catch (DataIntegrityViolationException e) {
            throw new InvalidRegisterDataException("Login already present");
        }
    }

    @Override
    public AccessToken loginUser(LoginInfo credentials) {
        AccountInfo userInfo =
                accountInfoRepository.findAccountInfoByLogin(credentials.getLogin());

        String passwordHash;
        try {
            passwordHash = passwordHasher.hashPassword(credentials.getPassword());
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerException();
        }

        if (userInfo == null || !userInfo.getPasswordHash().equals(passwordHash)) {
            throw new InvalidLoginCredentialsException();
        }

        AccessToken accessToken = AccessToken.builder()
                .userId(userInfo.getId())
                .build();

        return accessTokenRepository.save(accessToken);
    }

    @Override
    public AccountInfo authorizeUserByAccessToken(String accessToken) {
        UUID parsedToken;
        try {
            parsedToken = UUID.fromString(accessToken);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid access token format");
        }

        AccessToken userAccessToken =
                accessTokenRepository.findAccessTokenByToken(parsedToken);
        if (userAccessToken == null) {
            throw new UnauthorizedException("Access token in invalid");
        }

        AccountInfo authorizedUser =
                accountInfoRepository.findAccountInfoById(userAccessToken.getUserId());
        if (authorizedUser == null) {
            throw new UnauthorizedException("Access token in invalid");
        }

        return authorizedUser;
    }

    @Override
    public AccountInfo findUserByLogin(String login) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public AccountInfo findUserById(UUID id) {
        AccountInfo info = accountInfoRepository.findAccountInfoById(id);
        if (info == null) {
            throw new BadRequestException("Invalid user id");
        }
        return info;
    }

    @Override
    public List<AccountInfo> findUsersByListOfIds(List<UUID> ids) {
        return accountInfoRepository.findAllByIdIn(ids);
    }
}
