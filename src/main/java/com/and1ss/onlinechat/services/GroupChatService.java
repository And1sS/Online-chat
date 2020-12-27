package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.api.dto.GroupChatRetrievalDTO;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.AccountInfo;

import java.util.List;
import java.util.UUID;

public interface GroupChatService {
    GroupChat createGroupChat(GroupChat chat, List<AccountInfo> participants, AccountInfo author);
    GroupChat getGroupChatById(UUID id, AccountInfo author);
    GroupChatRetrievalDTO getGroupChatWithLastMessageDTOById(UUID id, AccountInfo author);
    void patchGroupChat(GroupChat chat, AccountInfo autor);

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
    GroupChatUser.MemberType getUserMemberType(GroupChat chat, AccountInfo author);
    List<AccountInfo> getGroupChatMembers(GroupChat chat, AccountInfo author);
    List<UUID> getGroupChatMembersIds(GroupChat chat, AccountInfo author);

    List<GroupChatRetrievalDTO> getAllGroupChatsWithLastMessageDTOForUser(AccountInfo user);
    List<GroupChatRetrievalDTO> getGroupChatsPageForUser(AccountInfo user);
}
