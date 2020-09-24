package com.and1ss.onlinechat.services.chat;

import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChat;
import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChatUser;
import com.and1ss.onlinechat.services.chat.model.private_chat.PrivateChat;
import com.and1ss.onlinechat.services.user.model.AccountInfo;

import java.util.List;
import java.util.UUID;

public interface ChatService {
        PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author);
        PrivateChat getPrivateChatById(UUID id, AccountInfo author);
        List<PrivateChat> getAllPrivateChatsForUser(AccountInfo user);
        List<PrivateChat> getPrivateChatsPageForUser(AccountInfo user);
        boolean userMemberOfPrivateChat(PrivateChat chat, AccountInfo author);

        GroupChat createGroupChat(GroupChat chat, List<AccountInfo> participants, AccountInfo author);
        GroupChat getGroupChatById(UUID id, AccountInfo author);

        void addUser(GroupChat chat, AccountInfo author, AccountInfo toBeAdded);
        void addUsers(GroupChat chat, AccountInfo author, List<AccountInfo> toBeAdded);
        void deleteUser(GroupChat chat, AccountInfo author, AccountInfo toBeDeleted);
        void changeUserMemberType(
                GroupChat chat,
                AccountInfo author,
                AccountInfo member,
                GroupChatUser.MemberType newMemberType
        );
        boolean userMemberOfGroupChat(GroupChat chat, AccountInfo author);
        List<AccountInfo> getGroupChatMembers(GroupChat chat, AccountInfo author);

        List<GroupChat> getAllGroupChatsForUser(AccountInfo user);
        List<GroupChat> getGroupChatsPageForUser(AccountInfo user);
}
