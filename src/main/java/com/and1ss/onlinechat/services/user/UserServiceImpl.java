package com.and1ss.onlinechat.services.user;

import com.and1ss.onlinechat.exceptions.InternalServerException;
import com.and1ss.onlinechat.exceptions.InvalidLoginCredentialsException;
import com.and1ss.onlinechat.exceptions.InvalidRegisterDataException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
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
        AccessToken userAccessToken =
                accessTokenRepository.findAccessTokenByToken(accessToken);
        if (accessToken == null) {
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
}
