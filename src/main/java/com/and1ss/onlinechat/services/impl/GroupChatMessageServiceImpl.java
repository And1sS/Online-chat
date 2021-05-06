package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.repositories.GroupMessageRepository;
import com.and1ss.onlinechat.domain.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GroupChatMessageServiceImpl implements GroupChatMessageService {

    private GroupChatService groupChatService;

    private GroupMessageRepository groupMessageRepository;

    @Autowired
    public GroupChatMessageServiceImpl(
            GroupChatService groupChatService,
            GroupMessageRepository groupMessageRepository
    ) {
        this.groupChatService = groupChatService;
        this.groupMessageRepository = groupMessageRepository;
    }

    @Override
    public List<GroupMessage> getAllMessages(UUID chatId, UUID authorId) {
        if (!groupChatService.userMemberOfGroupChat(chatId, authorId)) {
            throw new UnauthorizedException("This user can not view messages of this chat");
        }

        return groupMessageRepository.getGroupMessagesByChatId(chatId);
    }

    @Override
    public GroupMessage getLastMessage(GroupChat groupChat, AccountInfo author) {
        if (!groupChatService.userMemberOfGroupChat(groupChat.getId(), author.getId())) {
            throw new UnauthorizedException("This user can not view messages of this chat");
        }

        return groupMessageRepository.getLastGroupMessage(groupChat.getId());
    }

    @Override
    public GroupMessage addMessage(GroupChat groupChat, GroupMessage message, AccountInfo author) {
        if (!groupChatService.userMemberOfGroupChat(groupChat, author)) {
            throw new UnauthorizedException("This user can not write messages to this chat");
        }

        if (message.getContents().isEmpty()) {
            throw new BadRequestException("Message contents must not be empty");
        }

        return groupMessageRepository.save(message);
    }

    @Override
    public GroupMessage patchMessage(GroupChat groupChat, GroupMessage message, AccountInfo author) {
        try {
            groupMessageRepository.getOne(message.getId());
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }

        if (!userCanPatchMessage(groupChat, message, author)) {
            throw new UnauthorizedException("This user can not patch this message");
        }

        return groupMessageRepository.save(message);
    }

    @Override
    public GroupMessage getMessageById(UUID id) {
        try {
            return groupMessageRepository.getOne(id);
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }
    }

    private boolean userCanPatchMessage(GroupChat groupChat, GroupMessage message, AccountInfo user) {
        return groupChatService.userMemberOfGroupChat(groupChat, user) &&
                message.getAuthor().equals(user);
    }

    private boolean userCanDeleteMessage(GroupChat groupChat, GroupMessage message, AccountInfo user) {
        GroupChatUser.MemberType memberType;
        try {
            memberType = groupChatService.getUserMemberType(groupChat, user);
        } catch (BadRequestException e) {
            return false;
        }

        return memberType == GroupChatUser.MemberType.admin ||
                groupChat.getCreator().equals(user) ||
                message.getAuthor().equals(user);
    }

    @Override
    public void deleteMessage(GroupChat groupChat, GroupMessage message, AccountInfo author) {
        try {
            groupMessageRepository.getOne(message.getId());
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }

        if (!userCanDeleteMessage(groupChat, message, author)) {
            throw new UnauthorizedException("This user can not delete this message");
        }
        groupMessageRepository.delete(message);
    }
}
