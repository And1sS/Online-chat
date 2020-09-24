package com.and1ss.onlinechat.services.private_chat;

import com.and1ss.onlinechat.services.private_chat.model.PrivateChat;
import com.and1ss.onlinechat.services.user.model.AccountInfo;

import java.util.List;
import java.util.UUID;

public interface PrivateChatService {
    PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author);
    PrivateChat getPrivateChatById(UUID id, AccountInfo author);
    List<PrivateChat> getAllPrivateChatsForUser(AccountInfo user);
    List<PrivateChat> getPrivateChatsPageForUser(AccountInfo user);
    boolean userMemberOfPrivateChat(PrivateChat chat, AccountInfo author);
}
