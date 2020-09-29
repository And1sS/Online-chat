package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.services.group_chat.model.GroupMessage;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageRetrievalDTO {
    protected UUID id;

    private AccountInfoRetrievalDTO author;

    @NonNull
    private UUID chatId;

    private String contents;

    private Timestamp createdAt;

    public static GroupMessageRetrievalDTO
    fromGroupMessage(GroupMessage groupMessage) {
        AccountInfoRetrievalDTO authorDTO = AccountInfoRetrievalDTO
                .fromAccountInfo(groupMessage.getAuthor());

        return builder()
                .id(groupMessage.getId())
                .author(authorDTO)
                .chatId(groupMessage.getChatId())
                .contents(groupMessage.getContents())
                .createdAt(groupMessage.getCreatedAt())
                .build();
    }
}
