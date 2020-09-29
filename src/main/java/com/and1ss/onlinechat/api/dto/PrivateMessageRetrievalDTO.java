package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.services.private_chat.model.PrivateMessage;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageRetrievalDTO {
    protected UUID id;

    private AccountInfoRetrievalDTO author;

    @NonNull
    private UUID chatId;

    private String contents;

    private Timestamp createdAt;

    public static PrivateMessageRetrievalDTO
    fromPrivateMessage(PrivateMessage privateMessage) {
        AccountInfoRetrievalDTO authorDTO = AccountInfoRetrievalDTO
                .fromAccountInfo(privateMessage.getAuthor());

        return builder()
                .id(privateMessage.getId())
                .author(authorDTO)
                .chatId(privateMessage.getChatId())
                .contents(privateMessage.getContents())
                .createdAt(privateMessage.getCreatedAt())
                .build();
    }
}