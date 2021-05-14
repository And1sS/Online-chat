package com.and1ss.onlinechat.services.mappers;

import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.services.dto.GroupChatCreationDTO;
import com.and1ss.onlinechat.services.dto.GroupChatRetrievalDTO;

import java.util.ArrayList;

public class GroupChatMapper {
    public static GroupChat toGroupChat(GroupChatCreationDTO groupChatCreationDTO) {
        return GroupChat.builder()
                .title(groupChatCreationDTO.getTitle())
                .about(groupChatCreationDTO.getAbout())
                .groupChatUsers(new ArrayList<>())
                .build();
    }

    public static GroupChatRetrievalDTO toGroupChatRetrievalDTO(GroupChat groupChat) {
        return GroupChatRetrievalDTO.builder()
                .id(groupChat.getId())
                .title(groupChat.getTitle())
                .about(groupChat.getAbout())
                .creator(AccountInfoMapper.toAccountInfoRetrievalDTO(groupChat.getCreator()))
                .build();
    }
}
