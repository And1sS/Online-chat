package com.and1ss.onlinechat.services.chat.repos;

import com.and1ss.onlinechat.services.chat.model.PrivateChat;
import com.and1ss.onlinechat.services.user.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("private_chat")
public interface PrivateChatRepository extends JpaRepository<PrivateChat, UUID> {
    PrivateChat findPrivateChatById(UUID id);
}

