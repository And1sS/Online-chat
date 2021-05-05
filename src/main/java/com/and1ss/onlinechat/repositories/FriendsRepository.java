package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.repositories.projections.FriendsForUserProjection;
import com.and1ss.onlinechat.repositories.projections.FriendsWithoutPrivateChatProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, UUID> {
    @Query(
            value = "SELECT * FROM friends WHERE request_issuer_id = :userId " +
                    "OR requestee_id = :userId",
            nativeQuery = true
    )
    List<Friends> getFriendsByUserId(UUID userId);

    @Query(
            value = "SELECT * FROM friends WHERE " +
                    "(request_issuer_id = :user1Id AND requestee_id = :user2Id) OR " +
                    "(request_issuer_id = :user2Id AND requestee_id = :user1Id) ",
            nativeQuery = true
    )
    Friends getFriendsById(UUID user1Id, UUID user2Id);

    @Query(value = getFriendsForUserQuery, nativeQuery = true)
    List<FriendsForUserProjection> getFriendForUser(UUID userId);

    @Query(value = getAcceptedFriendsForUserQuery, nativeQuery = true)
    List<FriendsForUserProjection> getAcceptedFriendsForUser(UUID userId);

    @Query(value = getFriendsWithoutPrivateChatQuery, nativeQuery = true)
    List<FriendsWithoutPrivateChatProjection> getAcceptedFriendsWithoutPrivateChatForUser(UUID userId);

    String getFriendsForUserQuery =
            "SELECT DISTINCT cast(request_issuer.id AS text) AS requestIssuerId, " +
                    "       request_issuer.name             AS requestIssuerName, " +
                    "       request_issuer.surname          AS requestIssuerSurname, " +
                    "       request_issuer.login            AS requestIssuerLogin, " +
                    "       cast(requestee.id AS text)      AS requesteeId, " +
                    "       requestee.name                  AS requesteeName, " +
                    "       requestee.surname               AS requesteeSurname, " +
                    "       requestee.login                 AS requesteeLogin, " +
                    "       status " +
                    "FROM ( " +
                    "         SELECT * " +
                    "         FROM friends f " +
                    "         WHERE f.request_issuer_id = :userId " +
                    "            OR f.requestee_id = :userId " +
                    "     ) friends " +
                    "         INNER JOIN account_info request_issuer ON friends.request_issuer_id = request_issuer.id " +
                    "         INNER JOIN account_info requestee ON friends.requestee_id = requestee.id";

    String getAcceptedFriendsForUserQuery = getFriendsForUserQuery +
            " WHERE status = 'accepted'";

    String getFriendsWithoutPrivateChatQuery =
            "SELECT DISTINCT cast(account_info.id AS text) AS id, " +
                    "       account_info.name             AS name, " +
                    "       account_info.surname          AS surname, " +
                    "       account_info.name             AS login " +
                    "FROM (SELECT friends_where_current_request_issuer.requestee_id AS id " +
                    "      FROM ( " +
                    "               SELECT * " +
                    "               FROM friends f " +
                    "               WHERE f.request_issuer_id = :userId " +
                    "                 AND f.status = 'accepted' " +
                    "           ) friends_where_current_request_issuer " +
                    "      UNION ALL " +
                    "      SELECT friends_where_current_requestee.request_issuer_id AS id " +
                    "      FROM ( " +
                    "               SELECT * " +
                    "               FROM friends f " +
                    "               WHERE f.requestee_id = :userId " +
                    "                 AND f.status = 'accepted' " +
                    "           ) friends_where_current_requestee " +
                    "     ) accepted_friends_ids " +
                    "INNER JOIN account_info ON account_info.id = accepted_friends_ids.id " +
                    "WHERE account_info.id NOT IN ( " +
                    "    SELECT friends_1.user_1_id AS id " +
                    "    FROM ( " +
                    "             SELECT user_1_id " +
                    "             FROM private_chat pch " +
                    "             WHERE pch.user_2_id = :userId " +
                    "         ) friends_1 " +
                    "    UNION ALL " +
                    "    SELECT friends_2.user_2_id AS id " +
                    "    FROM ( " +
                    "             SELECT user_2_id " +
                    "             FROM private_chat pch " +
                    "             WHERE pch.user_1_id = :userId " +
                    "         ) friends_2 " +
                    ") ";
}
