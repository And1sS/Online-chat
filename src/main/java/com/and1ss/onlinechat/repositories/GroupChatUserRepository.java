package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupChatUserRepository extends JpaRepository<GroupChatUser, UUID> {
    Optional<GroupChatUser> findByGroupChatAndUser(GroupChat groupChat, AccountInfo user);

    @Query(value = "from GroupChatUser where id.userId = :userId")
    List<GroupChatUser> findAllByUserId(UUID userId);

    @Query(value = "from GroupChatUser where id.groupChatId = :chatId")
    List<GroupChatUser> findAllByChatId(UUID chatId);
}
