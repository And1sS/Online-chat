package com.and1ss.onlinechat.services.mappers;

import com.and1ss.onlinechat.domain.GroupMessage;
import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.dto.GroupMessageRetrievalDTO;
import com.and1ss.onlinechat.services.dto.GroupMessageCreationDTO;

public class GroupMessageMapper {
    public static GroupMessage toGroupMessage(GroupMessageCreationDTO creationDTO) {
        return GroupMessage.builder()
                .contents(creationDTO.getContents())
                .build();
    }

    public static GroupMessageRetrievalDTO toGroupMessageRetrievalDTO(GroupMessage groupMessage) {
        AccountInfoRetrievalDTO authorDTO = AccountInfoMapper
                .toAccountInfoRetrievalDTO(groupMessage.getAuthor());

        return GroupMessageRetrievalDTO.builder()
                .id(groupMessage.getId())
                .author(authorDTO)
                .chatId(groupMessage.getChat().getId())
                .contents(groupMessage.getContents())
                .createdAt(groupMessage.getCreatedAt())
                .build();
    }
}
