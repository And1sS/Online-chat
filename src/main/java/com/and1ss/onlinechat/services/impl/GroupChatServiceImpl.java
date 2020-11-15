package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.GroupChatUserId;
import com.and1ss.onlinechat.repositories.GroupChatRepository;
import com.and1ss.onlinechat.repositories.GroupChatUserRepository;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.domain.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupChatServiceImpl implements GroupChatService {

    private GroupChatRepository groupChatRepository;

    private GroupChatUserRepository groupChatUserJoinRepository;

    private UserService userService;

    @Autowired
    public GroupChatServiceImpl(
            GroupChatRepository groupChatRepository,
            GroupChatUserRepository groupChatUserJoinRepository,
            UserService userService
    ) {
        this.groupChatRepository = groupChatRepository;
        this.groupChatUserJoinRepository = groupChatUserJoinRepository;
        this.userService = userService;
    }

    @Override
    public GroupChat createGroupChat(
            GroupChat chat,
            List<AccountInfo> participants,
            AccountInfo author
    ) {
        if (chat.getTitle().isEmpty()) {
            throw new BadRequestException("Group chat must have not empty title");
        }

        if (!participants.contains(author)) {
            participants.add(author);
        }

        GroupChat createdChat;
        try {
            createdChat = groupChatRepository.save(chat);
        } catch (Exception e) {
            throw new BadRequestException("This chat is already present");
        }

        uncheckedAddUsers(chat, author, participants);

        return createdChat;
    }

    @Override
    public boolean userMemberOfGroupChat(GroupChat chat, AccountInfo user) {
        return getGroupChatUserJoin(chat, user) != null;
    }

    @Override
    public GroupChatUser.MemberType getUserMemberType(GroupChat chat, AccountInfo user) {
        GroupChatUser join = getGroupChatUserJoin(chat, user);

        if (join == null) {
            throw new BadRequestException("This user is not member of this chat");
        }

        return join.getMemberType();
    }

    private boolean userAdminOrCreator(GroupChat chat, AccountInfo user) {
        return chat.getCreator().equals(user) ||
                (getGroupChatUserJoin(chat, user).getMemberType()
                        == GroupChatUser.MemberType.admin);
    }

    private GroupChatUser getGroupChatUserJoin(GroupChat chat, AccountInfo user) {
        return groupChatUserJoinRepository
                .findByGroupChatIdAndUserId(chat.getId(), user.getId());
    }

    @Override
    public List<AccountInfo> getGroupChatMembers(GroupChat chat, AccountInfo author) {
        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }
        List<GroupChatUser> joins = groupChatUserJoinRepository.findAllByChatId(chat.getId());
        List<UUID> usersIds = joins.stream()
                .map(join -> join.getId().getUserId())
                .collect(Collectors.toList());

        return userService.findUsersByListOfIds(usersIds);
    }

    @Override
    public List<UUID> getGroupChatMembersIds(GroupChat chat, AccountInfo author) {
        return getGroupChatMembers(chat, author)
                .stream()
                .map(AccountInfo::getId)
                .collect(Collectors.toList());
    }

    @Override
    public GroupChat getGroupChatById(UUID id, AccountInfo author) {
        GroupChat chat;
        try {
            chat = groupChatRepository.getOne(id);
        } catch (Exception e) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        return chat;
    }

    @Override
    public void patchGroupChat(GroupChat chat, AccountInfo author) {
        if (!userAdminOrCreator(chat, author)) {
            throw new UnauthorizedException("This user can not patch this chat");
        }

        groupChatRepository.save(chat);
    }

    @Override
    public void addUser(GroupChat chat, AccountInfo author, AccountInfo toBeAdded) {
        if (groupChatRepository.findGroupChatById(chat.getId()) == null) {
            throw new BadRequestException("This chat does not exist");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        if (!userMemberOfGroupChat(chat, toBeAdded)) {
            GroupChatUserId compositeId = new GroupChatUserId(
                    chat.getId(),
                    toBeAdded.getId()
            );

            GroupChatUser join = GroupChatUser.builder()
                    .memberType(GroupChatUser.MemberType.readwrite)
                    .id(compositeId)
                    .build();

            groupChatUserJoinRepository.save(join);
        } else {
            throw new BadRequestException("This user is already member of this chat");
        }
    }

    @Override
    public void addUsers(GroupChat chat, AccountInfo author, List<AccountInfo> toBeAdded) {
        if (groupChatRepository.findGroupChatById(chat.getId()) == null) {
            throw new BadRequestException("This chat does not exist");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        uncheckedAddUsers(chat, author, toBeAdded);
    }

    // TODO: Now, this method assumes that author is chat creator
    // Fix this
    private void uncheckedAddUsers(
            GroupChat chat,
            AccountInfo author,
            List<AccountInfo> toBeAdded
    ) {
        Set<GroupChatUser> allUsersJoin = toBeAdded.stream()
                .filter(user -> !userMemberOfGroupChat(chat, user))
                .map(user -> {
                    GroupChatUser.MemberType memberType = GroupChatUser.MemberType.readwrite;
                    if (user.equals(author)) {
                        memberType = GroupChatUser.MemberType.admin;
                    }

                    return GroupChatUser.builder()
                            .id(new GroupChatUserId(chat.getId(), user.getId()))
                            .memberType(memberType)
                            .build();
                }).collect(Collectors.toSet());

        groupChatUserJoinRepository.saveAll(allUsersJoin);
    }

    @Override
    public void deleteUser(GroupChat chat, AccountInfo author, AccountInfo toBeDeleted) {
        GroupChatUser authorJoin = getGroupChatUserJoin(chat, author);
        GroupChatUser toBeDeletedJoin = getGroupChatUserJoin(chat, toBeDeleted);

        if (!userAdminOrCreator(chat, author)) {
            throw new UnauthorizedException("This user cannot delete members of this chat");
        }

        if (chat.getCreator().equals(toBeDeleted)) {
            throw new UnauthorizedException("This user cannot delete chat creator");
        }

        groupChatUserJoinRepository.delete(toBeDeletedJoin);
    }

    @Override
    public void changeUserMemberType(
            GroupChat chat,
            AccountInfo author,
            AccountInfo member,
            GroupChatUser.MemberType newMemberType
    ) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public List<GroupChat> getAllGroupChatsForUser(AccountInfo user) {
        List<GroupChatUser> joins = groupChatUserJoinRepository.findAllByUserId(user.getId());
        List<UUID> chatsIds = joins.stream()
                .map(join -> join.getId().getGroupChatId())
                .collect(Collectors.toList());
        return groupChatRepository.findAllByIdIn(chatsIds);
    }

    @Override
    public List<GroupChat> getGroupChatsPageForUser(AccountInfo user) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }
}
