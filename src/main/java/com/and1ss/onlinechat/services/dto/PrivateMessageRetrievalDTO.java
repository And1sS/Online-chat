package com.and1ss.onlinechat.services.dto;

import com.and1ss.onlinechat.domain.PrivateMessage;
import com.and1ss.onlinechat.services.mappers.AccountInfoMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageRetrievalDTO {
    @NonNull
    protected UUID id;

    private AccountInfoRetrievalDTO author;

    @NonNull
    @JsonProperty("chat_id")
    private UUID chatId;

    @NonNull
    private String contents;

    @JsonProperty("created_at")
    private Timestamp createdAt;

    public static PrivateMessageRetrievalDTO
    fromPrivateMessage(PrivateMessage privateMessage) {
        AccountInfoRetrievalDTO authorDTO = AccountInfoMapper
                .toAccountInfoRetrievalDTO(privateMessage.getAuthor());

        return builder()
                .id(privateMessage.getId())
                .author(authorDTO)
                .chatId(privateMessage.getChat().getId())
                .contents(privateMessage.getContents())
                .createdAt(privateMessage.getCreatedAt())
                .build();
    }
}