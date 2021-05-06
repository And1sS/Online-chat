package com.and1ss.onlinechat.services.dto;

import com.and1ss.onlinechat.domain.GroupChat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatRetrievalDTO {
    @NonNull
    private UUID id;

    @NonNull
    private String title;

    private String about;

    private AccountInfoRetrievalDTO creator;

    @JsonProperty("last_message")
    private GroupMessageRetrievalDTO lastMessage;
    public static GroupChatRetrievalDTO fromGroupChat(
            GroupChat groupChat,
            GroupMessageRetrievalDTO lastMessage
    ) {
        AccountInfoRetrievalDTO creatorDTO =
                AccountInfoRetrievalDTO.fromAccountInfo(groupChat.getCreator());

        return GroupChatRetrievalDTO.builder()
                .title(groupChat.getTitle())
                .id(groupChat.getId())
                .about(groupChat.getAbout())
                .creator(creatorDTO)
                .lastMessage(lastMessage)
                .build();
    }
}

