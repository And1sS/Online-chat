package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.domain.*;
import com.and1ss.onlinechat.exceptions.*;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.utils.password_hasher.PasswordHasher;
import com.and1ss.onlinechat.repositories.AccessTokenRepository;
import com.and1ss.onlinechat.repositories.AccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private AccountInfoRepository accountInfoRepository;

    private AccessTokenRepository accessTokenRepository;

    private PasswordHasher passwordHasher;

    private @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public UserServiceImpl(
            AccountInfoRepository accountInfoRepository,
            AccessTokenRepository accessTokenRepository,
            PasswordHasher passwordHasher
    ) {
        this.accountInfoRepository = accountInfoRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.passwordHasher = passwordHasher;
    }

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
                .user(userInfo)
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

        AccountInfo authorizedUser = userAccessToken.getUser();
        if (authorizedUser == null) {
            throw new UnauthorizedException("Access token in invalid");
        }

        return authorizedUser;
    }

    @Override
    public AccountInfo authorizeUserByBearerToken(String token) {
        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
        return authorizeUserByAccessToken(parsedAccessToken);
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

    private String findUsersWhoAreNotCurrentUserFriendsQueryString() {
        return "SELECT cast(id AS text), name, surname, login " +
                "FROM account_info " +
                "WHERE id NOT IN ( " +
                "    SELECT friends_where_current_request_issuer.requestee_id AS id " +
                "    FROM ( " +
                "             SELECT * " +
                "             FROM friends f " +
                "             WHERE f.request_issuer_id = :user_id " +
                "         ) friends_where_current_request_issuer " +
                "    UNION ALL " +
                "    SELECT friends_where_current_requestee.request_issuer_id AS id " +
                "    FROM ( " +
                "             SELECT * " +
                "             FROM friends f " +
                "             WHERE f.requestee_id = :user_id " +
                "         ) friends_where_current_requestee " +
                "    UNION ALL " +
                "    SELECT :user_id AS id " +
                ")";
    }

    private AccountInfoRetrievalDTO mapFromTuple(Tuple tuple) {
        final UUID id = getUUIDFromTupleOrNull(tuple, "id");
        final String name = (String) getFromTupleOrNull(tuple, "name");
        final String surname = (String) getFromTupleOrNull(tuple, "surname");
        final String login = (String) getFromTupleOrNull(tuple, "login");

        if (id == null || name == null || surname == null || login == null) {
            return null;
        }
        return new AccountInfoRetrievalDTO(id, name, surname, login);
    }

    private List<AccountInfoRetrievalDTO> findUsersByQuery(Query query) {
        return ((List<Tuple>) query.getResultList()).stream()
                .map(this::mapFromTuple)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<AccountInfoRetrievalDTO> findUsersWhoAreNotCurrentUserFriends(String accessToken) {
        AccountInfo user = authorizeUserByBearerToken(accessToken);
        final String queryString = findUsersWhoAreNotCurrentUserFriendsQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("user_id", user.getId());

        return findUsersByQuery(query);
    }


    private String findUsersWhoAreNotCurrentUserFriendsAndLoginLikeQueryString() {
        return findUsersWhoAreNotCurrentUserFriendsQueryString() +
                " AND login LIKE :login_like";
    }

    @Override
    public List<AccountInfoRetrievalDTO> findUsersWhoAreNotCurrentUserFriendsAndLoginLike(
            String accessToken, String loginLike
    ) {
        AccountInfo user = authorizeUserByBearerToken(accessToken);
        final String queryString = findUsersWhoAreNotCurrentUserFriendsAndLoginLikeQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("user_id", user.getId());
        query.setParameter("login_like", "%" + loginLike + "%");

        return findUsersByQuery(query);
    }
}
