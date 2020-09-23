package com.and1ss.onlinechat.services.chat;

import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.chat.model.PrivateChat;
import com.and1ss.onlinechat.services.chat.repos.PrivateChatRepository;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
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
    public void deletePrivateChat(PrivateChat chat, AccountInfo author) {

    }
}
