package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.GroupChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GroupChatUserRepository extends JpaRepository<GroupChatUser, UUID> {
    @Query(
            value = "SELECT * FROM group_user WHERE " +
                    "group_chat_id=:groupChatId " +
                    "AND user_id=:userId",
            nativeQuery = true
    )
    GroupChatUser findByGroupChatIdAndUserId(UUID groupChatId, UUID userId);

    @Query(
            value = "SELECT * FROM group_user WHERE user_id=:userId",
            nativeQuery = true
    )
    List<GroupChatUser> findAllByUserId(UUID userId);

    @Query(
            value = "SELECT * FROM group_user WHERE group_chat_id=:chatId",
            nativeQuery = true
    )
    List<GroupChatUser> findAllByChatId(UUID chatId);
}
