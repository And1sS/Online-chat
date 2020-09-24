package com.and1ss.onlinechat.services.private_chat;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.exceptions.UnimplementedException;
import com.and1ss.onlinechat.services.private_chat.model.PrivateChat;
import com.and1ss.onlinechat.services.private_chat.repos.PrivateChatRepository;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class PrivateChatServiceImpl implements PrivateChatService {
    @Autowired
    private PrivateChatRepository privateChatRepository;

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
    public PrivateChat getPrivateChatById(UUID id, AccountInfo author) {
        PrivateChat chat;
        try {
            chat = privateChatRepository.getOne(id);
        } catch (EntityNotFoundException e) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        if (!userMemberOfPrivateChat(chat, author)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        return chat;
    }

    @Override
    public List<PrivateChat> getAllPrivateChatsForUser(AccountInfo user) {
        return privateChatRepository.findPrivateChatsByUserId(user.getId());
    }

    @Override
    public List<PrivateChat> getPrivateChatsPageForUser(AccountInfo user) {
        throw new UnimplementedException();
    }

    @Override
    public boolean userMemberOfPrivateChat(PrivateChat chat, AccountInfo author) {
        return chat.getUser1().equals(author) || chat.getUser2().equals(author);
    }
}
