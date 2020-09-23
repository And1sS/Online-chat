package com.and1ss.onlinechat.services.chat;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChat;
import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChatUserJoin;
import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChatUserJoinId;
import com.and1ss.onlinechat.services.chat.model.private_chat.PrivateChat;
import com.and1ss.onlinechat.services.chat.repos.GroupChatRepository;
import com.and1ss.onlinechat.services.chat.repos.GroupChatUserJoinRepository;
import com.and1ss.onlinechat.services.chat.repos.PrivateChatRepository;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChatUserJoin.MemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private GroupChatUserJoinRepository groupChatUserJoinRepository;

    @Override
    public PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author) {
        if (!chat.getUser1().equals(author) && !chat.getUser2().equals(author)) {
            throw new UnauthorizedException("This user can't create this chat");
        }

        PrivateChat privateChat;
        try {
            privateChat = privateChatRepository.save(chat);
        } catch (Exception e) {
            throw new BadRequestException("This chat is already present");
        }

        return privateChat;
    }


    @Override
    public GroupChat createGroupChat(GroupChat chat, List<AccountInfo> participants, AccountInfo author) {
        if (!participants.contains(author)) {
            throw new BadRequestException("Creator should also be a participant");
        }

        GroupChat createdChat;
        try {
            createdChat = groupChatRepository.save(chat);
        } catch (Exception e) {
            throw new BadRequestException("This chat already exists");
        }

        uncheckedAddUsers(chat, author, participants);

        return createdChat;
    }

    @Override
    public boolean userMemberOfGroupChat(GroupChat chat, AccountInfo user) {
        GroupChatUserJoin join = groupChatUserJoinRepository
                .getByGroupChatIdAndUserId(chat.getId(), user.getId());
        return join != null;
    }

    @Override
    public void addUser(GroupChat chat, AccountInfo author, AccountInfo toBeAdded) {
        if (groupChatRepository.getGroupChatById(chat.getId()) == null) {
            throw new BadRequestException("This chat does not exist");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        if (!userMemberOfGroupChat(chat, toBeAdded)) {
            GroupChatUserJoinId compositeId = new GroupChatUserJoinId(
                    chat.getId(),
                    toBeAdded.getId()
            );

            GroupChatUserJoin join = GroupChatUserJoin.builder()
                    .memberType(MemberType.readwrite)
                    .id(compositeId)
                    .build();

            groupChatUserJoinRepository.save(join);
        }
    }

    @Override
    public void addUsers(GroupChat chat, AccountInfo author, List<AccountInfo> toBeAdded) {
        if (groupChatRepository.getGroupChatById(chat.getId()) == null) {
            throw new BadRequestException("This chat does not exist");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        uncheckedAddUsers(chat, author, toBeAdded);
    }

    // utility function to add users after all checks
    private void uncheckedAddUsers(
            GroupChat chat,
            AccountInfo author,
            List<AccountInfo> toBeAdded
    ) {
        Set<GroupChatUserJoin> allUsersJoin = new HashSet<>();
        for (AccountInfo user : toBeAdded) {
            if (userMemberOfGroupChat(chat, user)) {
                continue;
            }

            MemberType memberType = MemberType.readwrite;
            if (user.equals(author)) {
                memberType = MemberType.admin;
            }

            GroupChatUserJoin join = GroupChatUserJoin.builder()
                    .id(new GroupChatUserJoinId(chat.getId(), user.getId()))
                    .memberType(memberType)
                    .build();

            allUsersJoin.add(join);
        }
        groupChatUserJoinRepository.saveAll(allUsersJoin);
    }

    @Override
    public void deleteUser(GroupChat chat, AccountInfo author, AccountInfo toBeDeleted) {
        GroupChatUserJoin authorJoin = groupChatUserJoinRepository
                .getByGroupChatIdAndUserId(chat.getId(), author.getId());
        GroupChatUserJoin toBeDeletedJoin = groupChatUserJoinRepository
                .getByGroupChatIdAndUserId(chat.getId(), author.getId());

        if (!userMemberOfGroupChat(chat, author) &&
                authorJoin.getMemberType() != MemberType.admin) {
            throw new UnauthorizedException("This user cannot delete members of this chat");
        }

        if (chat.getCreator().equals(toBeDeleted)) {
            throw new UnauthorizedException("This user cannot delete chat creator");
        }

        groupChatUserJoinRepository.delete(toBeDeletedJoin);
    }
}
