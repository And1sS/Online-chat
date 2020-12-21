package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.repositories.FriendsRepository;
import com.and1ss.onlinechat.services.FriendsService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.*;

@Service
@Transactional
public class FriendsServiceImpl implements FriendsService {

    private final FriendsRepository friendsRepository;

    private @PersistenceContext
    EntityManager entityManager;

    public FriendsServiceImpl(FriendsRepository friendsRepository) {
        this.friendsRepository = friendsRepository;
    }

    @Override
    public Friends createFriendRequest(Friends friends, AccountInfo author) {
        Friends usersFriends = isUsersFriends(
                friends.getId().getRequesteeId(),
                friends.getId().getRequestIssuerId()
        );

        if (usersFriends != null) {
            throw new BadRequestException("This users are already friends");
        }

        return friendsRepository.save(friends);
    }

    private String getFriendsForUserQueryString() {
        return "SELECT  cast(request_issuer.id AS text) AS request_issuer_id, " +
                "       request_issuer.name             AS request_issuer_name, " +
                "       request_issuer.surname          AS request_issuer_surname, " +
                "       request_issuer.login            AS request_issuer_login, " +
                "       cast(requestee.id AS text)      AS requestee_id, " +
                "       requestee.name                  AS requestee_name, " +
                "       requestee.surname               AS requestee_surname, " +
                "       requestee.login                 AS requestee_login, " +
                "       status " +
                "FROM ( " +
                "         SELECT * " +
                "         FROM friends f " +
                "         WHERE f.request_issuer_id = :user_id " +
                "            OR f.requestee_id = :user_id " +
                "     ) friends " +
                "         INNER JOIN account_info request_issuer ON friends.request_issuer_id = request_issuer.id " +
                "         INNER JOIN account_info requestee ON friends.requestee_id = requestee.id";
    }

    public List<FriendRetrievalDTO> getFriendsForUserDTO(AccountInfo user) {
        final String queryString = getFriendsForUserQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("user_id", user.getId());

        return ((List<Tuple>) query.getResultList()).stream()
                .map(this::mapFromTuple)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private FriendRetrievalDTO mapFromTuple(Tuple tuple) {
        final UUID requestIssuerId = getUUIDFromTupleOrNull(tuple, "request_issuer_id");
        final String requestIssuerName = (String) getFromTupleOrNull(tuple, "request_issuer_name");
        final String requestIssuerSurname = (String) getFromTupleOrNull(tuple, "request_issuer_surname");
        final String requestIssuerLogin = (String) getFromTupleOrNull(tuple, "request_issuer_login");
        final UUID requesteeId = getUUIDFromTupleOrNull(tuple, "request_issuer_id");
        final String requesteeName = (String) getFromTupleOrNull(tuple, "requestee_name");
        final String requesteeSurname = (String) getFromTupleOrNull(tuple, "requestee_surname");
        final String requesteeLogin = (String) getFromTupleOrNull(tuple, "requestee_login");
        final Friends.FriendshipStatus status = (Friends.FriendshipStatus) getEnumFromTupleOrNull(
                tuple, "status", Friends.FriendshipStatus.class);

        if (requestIssuerId == null || requestIssuerName == null
                || requestIssuerSurname == null || requestIssuerLogin == null
                || requesteeId == null || requesteeName == null
                || requesteeSurname == null || requesteeLogin == null
                || status == null
        ) return null;

        AccountInfoRetrievalDTO requestIssuerDto = new AccountInfoRetrievalDTO(requestIssuerId,
                requestIssuerName, requestIssuerSurname, requestIssuerLogin);

        AccountInfoRetrievalDTO requesteeDto = new AccountInfoRetrievalDTO(requesteeId,
                requesteeName, requesteeSurname, requesteeLogin);

        return new FriendRetrievalDTO(requestIssuerDto, requesteeDto, status);
    }

    @Override
    public Friends isUsersFriends(UUID user1Id, UUID user2Id) {
        return friendsRepository.getFriendsById(user1Id, user2Id);
    }
}
