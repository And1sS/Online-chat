package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.services.model.GroupChat;
import com.and1ss.onlinechat.services.model.GroupMessage;
import com.and1ss.onlinechat.services.model.AccountInfo;

import java.util.List;
import java.util.UUID;

public interface GroupChatMessageService {
    List<GroupMessage> getAllMessages(GroupChat groupChat, AccountInfo author);
    GroupMessage addMessage(GroupChat groupChat, GroupMessage message, AccountInfo author);
    GroupMessage patchMessage(GroupChat groupChat, GroupMessage message, AccountInfo author);
    GroupMessage getMessageById(UUID id);
    void deleteMessage(GroupChat groupChat, GroupMessage message, AccountInfo author);
}
