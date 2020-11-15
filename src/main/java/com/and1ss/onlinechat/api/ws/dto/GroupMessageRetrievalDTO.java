package com.and1ss.onlinechat.api.ws.dto;

import com.and1ss.onlinechat.api.rest.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.domain.GroupMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageRetrievalDTO {
    @NonNull
    protected UUID id;

    private AccountInfoRetrievalDTO author;

    @NonNull
    @JsonProperty("chat_id")
    private UUID chatId;

    @NonNull
    private String contents;

    @NonNull
    @JsonProperty("created_at")
    private Timestamp createdAt;

    public static GroupMessageRetrievalDTO
    fromGroupMessage(GroupMessage groupMessage) {
        AccountInfoRetrievalDTO authorDTO = AccountInfoRetrievalDTO
                .fromAccountInfo(groupMessage.getAuthor());

        return builder()
                .id(groupMessage.getId())
                .author(authorDTO)
                .chatId(groupMessage.getChat().getId())
                .contents(groupMessage.getContents())
                .createdAt(groupMessage.getCreatedAt())
                .build();
    }
}
