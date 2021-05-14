package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.services.dto.GroupMessageCreationDTO;
import com.and1ss.onlinechat.services.dto.GroupMessagePatchDTO;
import com.and1ss.onlinechat.services.dto.GroupMessageRetrievalDTO;

import java.util.List;
import java.util.UUID;

public interface GroupChatMessageService {
    List<GroupMessageRetrievalDTO> getAllMessages(UUID chatId, UUID authorId);

    GroupMessageRetrievalDTO getLastMessage(UUID chatId, UUID authorId);

    GroupMessageRetrievalDTO addMessage(UUID chatId, GroupMessageCreationDTO creationDTO, UUID authorId);

    GroupMessageRetrievalDTO patchMessage(UUID messageId, GroupMessagePatchDTO patchDTO, UUID authorId);

    GroupMessageRetrievalDTO getMessageById(UUID messageId, UUID requesterId);

    void deleteMessage(UUID messageId, UUID authorId);
}
