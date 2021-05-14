package com.and1ss.onlinechat.services.dto;

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
}

