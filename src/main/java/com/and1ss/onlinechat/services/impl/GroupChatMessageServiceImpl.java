package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.repositories.AccountInfoRepository;
import com.and1ss.onlinechat.repositories.GroupChatRepository;
import com.and1ss.onlinechat.repositories.GroupMessageRepository;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.dto.GroupMessageCreationDTO;
import com.and1ss.onlinechat.services.dto.GroupMessagePatchDTO;
import com.and1ss.onlinechat.services.dto.GroupMessageRetrievalDTO;
import com.and1ss.onlinechat.services.mappers.GroupMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupChatMessageServiceImpl implements GroupChatMessageService {

    private GroupChatService groupChatService;

    private GroupChatRepository groupChatRepository;

    private AccountInfoRepository accountInfoRepository;

    private GroupMessageRepository groupMessageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public GroupChatMessageServiceImpl(
            GroupChatService groupChatService,
            GroupMessageRepository groupMessageRepository,
            GroupChatRepository groupChatRepository,
            AccountInfoRepository accountInfoRepository
    ) {
        this.groupChatService = groupChatService;
        this.groupMessageRepository = groupMessageRepository;
        this.groupChatRepository = groupChatRepository;
        this.accountInfoRepository = accountInfoRepository;
    }

    @Override
    public List<GroupMessageRetrievalDTO> getAllMessages(UUID chatId, UUID requesterId) {
        if (!groupChatService.userMemberOfGroupChat(chatId, requesterId)) {
            throw new UnauthorizedException("This user can not view messages of this chat");
        }

        List<GroupMessage> groupMessages = groupMessageRepository.getGroupMessagesByChatId(chatId);
        return groupMessages.stream()
                .map(GroupMessageMapper::toGroupMessageRetrievalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GroupMessageRetrievalDTO getLastMessage(UUID chatId, UUID requesterId) {
        if (!groupChatService.userMemberOfGroupChat(chatId, requesterId)) {
            throw new UnauthorizedException("This user can not view messages of this chat");
        }

        GroupMessage groupMessage = groupMessageRepository.getLastGroupMessage(chatId);
        return GroupMessageMapper.toGroupMessageRetrievalDTO(groupMessage);
    }

    @Override
    public GroupMessageRetrievalDTO addMessage(
            UUID chatId,
            GroupMessageCreationDTO creationDTO,
            UUID authorId
    ) {
        boolean isMember = groupChatService.userMemberOfGroupChat(chatId, authorId);
        GroupChatUser.MemberType memberType = groupChatService.getUserMemberType(chatId, authorId);

        if (!isMember || memberType.equals(GroupChatUser.MemberType.read)) {
            throw new UnauthorizedException("This user can not write messages to this chat");
        }

        if (creationDTO.getContents().isEmpty()) {
            throw new BadRequestException("Message contents must not be empty");
        }

        GroupChat groupChat = groupChatRepository.findById(chatId).orElseThrow();
        AccountInfo author = accountInfoRepository.findById(authorId).orElseThrow();

        GroupMessage message = GroupMessageMapper.toGroupMessage(creationDTO);
        message.setChat(groupChat);
        message.setAuthor(author);

        groupMessageRepository.save(message);
        entityManager.flush();

        return GroupMessageMapper.toGroupMessageRetrievalDTO(message);
    }

    @Override
    public GroupMessageRetrievalDTO patchMessage(
            UUID messageId,
            GroupMessagePatchDTO patchDTO,
            UUID authorId
    ) {
        GroupMessage message = groupMessageRepository.findById(messageId).orElseThrow();
        AccountInfo author = accountInfoRepository.findById(authorId).orElseThrow();

        if (!userCanPatchMessage(message, author)) {
            throw new UnauthorizedException("This user can not patch this message");
        }

        if (patchDTO.getContents().isEmpty()) {
            throw new BadRequestException("Message can not have empty content");
        }

        message.setContents(patchDTO.getContents());
        groupMessageRepository.save(message);

        return GroupMessageMapper.toGroupMessageRetrievalDTO(message);
    }

    @Override
    public GroupMessageRetrievalDTO getMessageById(UUID messageId, UUID requesterId) {
        GroupMessage message = groupMessageRepository.findById(messageId).orElseThrow();
        if (!groupChatService.userMemberOfGroupChat(message.getChat().getId(), requesterId)) {
            throw new UnauthorizedException("This user can view this message");
        }

        return GroupMessageMapper.toGroupMessageRetrievalDTO(message);
    }

    @Override
    public void deleteMessage(UUID messageId, UUID requesterId) {
        GroupMessage message = groupMessageRepository.findById(messageId).orElseThrow();
        AccountInfo author = accountInfoRepository.findById(requesterId).orElseThrow();

        if (!userCanDeleteMessage(message, author)) {
            throw new UnauthorizedException("This user can not delete this message");
        }
        groupMessageRepository.delete(message);
    }

    private boolean userCanPatchMessage(GroupMessage message, AccountInfo user) {
        return groupChatService.userMemberOfGroupChat(message.getChat().getId(), user.getId()) &&
                message.getAuthor().equals(user);
    }

    private boolean userCanDeleteMessage(GroupMessage message, AccountInfo user) {
        GroupChatUser.MemberType memberType;
        try {
            memberType = groupChatService
                    .getUserMemberType(message.getChat().getId(), user.getId());
        } catch (BadRequestException e) {
            return false;
        }

        return memberType == GroupChatUser.MemberType.admin ||
                message.getAuthor().equals(user);
    }
}
