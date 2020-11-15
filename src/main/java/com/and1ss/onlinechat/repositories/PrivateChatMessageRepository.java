package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrivateChatMessageRepository extends JpaRepository<PrivateMessage, UUID> {
    List<PrivateMessage> getPrivateMessagesByChatId(UUID chatId);
}
