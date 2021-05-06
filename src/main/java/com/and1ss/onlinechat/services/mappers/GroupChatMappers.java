package com.and1ss.onlinechat.services.mappers;

import com.and1ss.onlinechat.api.dto.GroupChatCreationDTO;
import com.and1ss.onlinechat.domain.GroupChat;

import java.util.ArrayList;

public class GroupChatMappers {
    public static GroupChat toGroupChat(GroupChatCreationDTO groupChatCreationDTO) {
        return GroupChat.builder()
                .title(groupChatCreationDTO.getTitle())
                .about(groupChatCreationDTO.getAbout())
                .groupChatUsers(new ArrayList<>())
                .build();
    }
}
