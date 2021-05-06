package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.api.dto.GroupChatCreationDTO;
import com.and1ss.onlinechat.api.dto.GroupChatPatchDTO;
import com.and1ss.onlinechat.api.dto.GroupChatRetrievalDTO;
import com.and1ss.onlinechat.domain.GroupChatUser;

import java.util.List;
import java.util.UUID;

public interface GroupChatService {
    GroupChatRetrievalDTO createGroupChat(GroupChatCreationDTO chatCreationDTO, UUID creatorId);

    GroupChatRetrievalDTO getGroupChatById(UUID id, UUID userId);

    void patchGroupChat(UUID chatId, GroupChatPatchDTO patchDTO, UUID author);

    void addUser(UUID chatId, UUID user, UUID authorId);

    void addUsers(UUID chatId, List<UUID> usersIds, UUID authorId);

    void deleteUser(UUID chatId, UUID user, UUID authorId);

    void changeUserMemberType(UUID chatId, UUID userId, UUID authorId, GroupChatUser.MemberType newMemberType);

    boolean userMemberOfGroupChat(UUID chatId, UUID authorId);

    GroupChatUser.MemberType getUserMemberType(UUID chatId, UUID authorId);

    List<AccountInfoRetrievalDTO> getGroupChatMembers(UUID chatId, UUID authorId);

    List<UUID> getGroupChatMembersIds(UUID chatId, UUID authorId);

    List<GroupChatRetrievalDTO> getAllGroupChatsForUser(UUID authorId);

    List<GroupChatRetrievalDTO> getGroupChatsPageForUser(UUID authorId);
}
