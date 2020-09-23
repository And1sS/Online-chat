package com.and1ss.onlinechat.services.chat;

import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChat;
import com.and1ss.onlinechat.services.chat.model.private_chat.PrivateChat;
import com.and1ss.onlinechat.services.user.model.AccountInfo;

import java.util.List;

public interface ChatService {
        PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author);

        GroupChat createGroupChat(GroupChat chat, List<AccountInfo> participants, AccountInfo author);
        boolean userMemberOfGroupChat(GroupChat chat, AccountInfo author);
        void addUser(GroupChat chat, AccountInfo author, AccountInfo toBeAdded);
        void addUsers(GroupChat chat, AccountInfo author, List<AccountInfo> toBeAdded);
        void deleteUser(GroupChat chat, AccountInfo author, AccountInfo toBeDeleted);
}
