package com.and1ss.onlinechat.services.impl.repos;

import com.and1ss.onlinechat.services.model.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrivateChatMessageRepository extends JpaRepository<PrivateMessage, UUID> {
    List<PrivateMessage> getPrivateMessagesByChatId(UUID chatId);
}
