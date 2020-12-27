package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.domain.FriendsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, UUID> {
    @Query(
            value = "SELECT * FROM friends WHERE request_issuer_id = :userId" +
                    " OR requestee_id = :userId",
            nativeQuery = true
    )
    List<Friends> getFriendsByUserId(UUID userId);

    @Query(
            value = "SELECT * FROM friends WHERE " +
                    "(request_issuer_id = :user1Id AND requestee_id = :user2Id) OR" +
                    "(request_issuer_id = :user2Id AND requestee_id = :user1Id) ",
            nativeQuery = true
    )
    Friends getFriendsById(UUID user1Id, UUID user2Id);
}
