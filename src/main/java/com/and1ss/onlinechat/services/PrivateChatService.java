package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.api.dto.PrivateChatRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.PrivateChat;

import java.util.List;
import java.util.UUID;

public interface PrivateChatService {
    PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author);

    PrivateChat getPrivateChatById(UUID id, AccountInfo author);

    PrivateChatRetrievalDTO getPrivateChatWithLastMessageDTOById(UUID id, AccountInfo author);

    List<PrivateChat> getAllPrivateChatsForUser(AccountInfo user);

    List<PrivateChat> getPrivateChatsPageForUser(AccountInfo user);

    boolean userMemberOfPrivateChat(PrivateChat chat, AccountInfo author);

    List<PrivateChatRetrievalDTO> getAllPrivateChatsWithLastMessageDTOForUser(AccountInfo user);
}
