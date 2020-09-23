package com.and1ss.onlinechat.services.chat.repos;

import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChatUserJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface GroupChatUserJoinRepository extends JpaRepository<GroupChatUserJoin, UUID> {
    @Query(
            value = "SELECT * FROM group_user WHERE " +
                    "group_chat_id=:groupChatId " +
                    "AND user_id=:userId",
            nativeQuery = true
    )
    GroupChatUserJoin getByGroupChatIdAndUserId(UUID groupChatId, UUID userId);
}
