package com.and1ss.onlinechat.services.dto;

import com.and1ss.onlinechat.domain.PrivateChat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChatRetrievalDTO {
    @NonNull
    private UUID id;

    @JsonProperty("user_1")
    private AccountInfoRetrievalDTO user1;

    @JsonProperty("user_2")
    private AccountInfoRetrievalDTO user2;

    @JsonProperty("last_message")
    private PrivateMessageRetrievalDTO lastMessage;

    public static PrivateChatRetrievalDTO fromPrivateChat(
            PrivateChat privateChat,
            PrivateMessageRetrievalDTO lastMessage
    ) {
        AccountInfoRetrievalDTO user1 =
                AccountInfoRetrievalDTO.fromAccountInfo(privateChat.getUser1());
        AccountInfoRetrievalDTO user2 =
                AccountInfoRetrievalDTO.fromAccountInfo(privateChat.getUser2());

        return PrivateChatRetrievalDTO.builder()
                .id(privateChat.getId())
                .user1(user1)
                .user2(user2)
                .lastMessage(lastMessage)
                .build();
    }
}