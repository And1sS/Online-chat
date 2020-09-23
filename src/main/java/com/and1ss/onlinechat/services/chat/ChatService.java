package com.and1ss.onlinechat.services.chat;

import com.and1ss.onlinechat.services.chat.model.PrivateChat;
import com.and1ss.onlinechat.services.user.model.AccountInfo;

public interface ChatService {
        PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author);
        void deletePrivateChat(PrivateChat chat, AccountInfo author);
//        void deleteChat(AbstractChat chat, User author) throws IllegalAccessException;
//        void sendMessage(AbstractChat chat, User author, Message message) throws IllegalAccessException;
//        void editMessage(AbstractChat chat, User author, Message message) throws IllegalAccessException;
//        void deleteMessage(AbstractChat chat, User author, Message message) throws IllegalAccessException;
//        void addUser(AbstractChat chat, User author, User toBeAdded) throws IllegalAccessException;
//        void deleteUser(AbstractChat chat, User author, User toBeDeleted) throws IllegalAccessException;
//        List<AbstractChat> getReadableChatsForUser(User user);
}
