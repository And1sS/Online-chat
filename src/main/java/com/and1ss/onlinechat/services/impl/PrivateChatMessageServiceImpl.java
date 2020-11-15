package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.PrivateChatMessageService;
import com.and1ss.onlinechat.services.PrivateChatService;
import com.and1ss.onlinechat.domain.PrivateChat;
import com.and1ss.onlinechat.domain.PrivateMessage;
import com.and1ss.onlinechat.repositories.PrivateChatMessageRepository;
import com.and1ss.onlinechat.domain.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PrivateChatMessageServiceImpl implements PrivateChatMessageService {

    private PrivateChatService privateChatService;

    private PrivateChatMessageRepository privateChatMessageRepository;

    @Autowired
    public PrivateChatMessageServiceImpl(
            PrivateChatService privateChatService,
            PrivateChatMessageRepository privateChatMessageRepository
    ) {
        this.privateChatService = privateChatService;
        this.privateChatMessageRepository = privateChatMessageRepository;
    }

    @Override
    public List<PrivateMessage> getAllMessages(PrivateChat privateChat, AccountInfo author) {
        if (!privateChatService.userMemberOfPrivateChat(privateChat, author)) {
            throw new UnauthorizedException("This user can not view messages of this chat");
        }

        return privateChat.getMessages();
    }

    @Override
    public PrivateMessage addMessage(
            PrivateChat privateChat,
            PrivateMessage message,
            AccountInfo author
    ) {
        if (!privateChatService.userMemberOfPrivateChat(privateChat, author)) {
            throw new UnauthorizedException("This user can not write messages to this chat");
        }

        if (message.getContents().isEmpty()) {
            throw new BadRequestException("Message contents must not be empty");
        }
        message.setAuthor(author);
        message.setChat(privateChat);

        return privateChatMessageRepository.save(message);
    }

    @Override
    public PrivateMessage patchMessage(
            PrivateChat privateChat,
            PrivateMessage message,
            AccountInfo author
    ) {
        try {
            privateChatMessageRepository.getOne(message.getId());
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }

        if (!message.getAuthor().equals(author)) {
            throw new UnauthorizedException("This user can not patch this message");
        }

        return privateChatMessageRepository.save(message);
    }

    @Override
    public PrivateMessage getMessageById(UUID id) {
        try {
            return privateChatMessageRepository.getOne(id);
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }
    }

    @Override
    public void deleteMessage(
            PrivateChat privateChat,
            PrivateMessage message,
            AccountInfo author
    ) {
        try {
            privateChatMessageRepository.getOne(message.getId());
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }

        if (!privateChatService
                .userMemberOfPrivateChat(privateChat, author)) {
            throw new UnauthorizedException("This user can not delete this message");
        }
        privateChatMessageRepository.delete(message);
    }
}
