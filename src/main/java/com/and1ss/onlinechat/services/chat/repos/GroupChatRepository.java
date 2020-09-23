package com.and1ss.onlinechat.services.chat.repos;

import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("group_user")
public interface GroupChatRepository extends JpaRepository<GroupChat, UUID> {
    GroupChat getGroupChatById(UUID id);
}
