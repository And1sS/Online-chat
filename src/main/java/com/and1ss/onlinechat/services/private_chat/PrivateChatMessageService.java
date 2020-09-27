package com.and1ss.onlinechat.services.private_chat;

import com.and1ss.onlinechat.services.group_chat.model.GroupChat;
import com.and1ss.onlinechat.services.group_chat.model.GroupMessage;
import com.and1ss.onlinechat.services.private_chat.model.PrivateChat;
import com.and1ss.onlinechat.services.private_chat.model.PrivateMessage;
import com.and1ss.onlinechat.services.user.model.AccountInfo;

import java.util.List;
import java.util.UUID;

public interface PrivateChatMessageService {
    List<PrivateMessage> getAllMessages(PrivateChat privateChat, AccountInfo author);
    PrivateMessage addMessage(PrivateChat privateChat, PrivateMessage message, AccountInfo author);
    PrivateMessage patchMessage(PrivateChat privateChat, PrivateMessage message, AccountInfo author);
    PrivateMessage getMessageById(UUID id);
    void deleteMessage(PrivateChat privateChat, PrivateMessage message, AccountInfo author);
}
