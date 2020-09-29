package com.and1ss.onlinechat.services.private_chat.impl.repos;

import com.and1ss.onlinechat.services.private_chat.model.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrivateChatMessageRepository extends JpaRepository<PrivateMessage, UUID> {
    List<PrivateMessage> getPrivateMessagesByChatId(UUID chatId);
}
