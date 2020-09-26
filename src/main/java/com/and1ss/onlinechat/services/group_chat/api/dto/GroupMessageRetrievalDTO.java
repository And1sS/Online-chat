package com.and1ss.onlinechat.services.group_chat.api.dto;

import com.and1ss.onlinechat.services.group_chat.model.GroupMessage;
import com.and1ss.onlinechat.services.user.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
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
