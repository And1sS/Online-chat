package com.and1ss.onlinechat.repositories;


import com.and1ss.onlinechat.domain.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("group_message")
public interface GroupMessageRepository extends JpaRepository<GroupMessage, UUID> {
    List<GroupMessage> getGroupMessagesByChatId(UUID chatId);
}
