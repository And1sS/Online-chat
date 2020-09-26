package com.and1ss.onlinechat.services.group_chat;

import com.and1ss.onlinechat.services.group_chat.model.GroupChat;
import com.and1ss.onlinechat.services.group_chat.model.GroupMessage;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface GroupChatMessageService {
    List<GroupMessage> getAllMessages(GroupChat groupChat, AccountInfo author);
    GroupMessage addMessage(GroupChat groupChat, GroupMessage message, AccountInfo author);
    GroupMessage patchMessage(GroupChat groupChat, GroupMessage message, AccountInfo author);
    GroupMessage getMessageById(UUID id);
    void deleteMessage(GroupChat groupChat, GroupMessage message, AccountInfo author);
}
