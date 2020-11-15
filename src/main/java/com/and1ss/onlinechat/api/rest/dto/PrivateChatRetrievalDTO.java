package com.and1ss.onlinechat.api.rest.dto;

import com.and1ss.onlinechat.domain.PrivateChat;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChatRetrievalDTO {
    @NonNull
    private UUID id;

    private AccountInfoRetrievalDTO user1;

    private AccountInfoRetrievalDTO user2;

    public static PrivateChatRetrievalDTO fromPrivateChat(PrivateChat privateChat) {
        AccountInfoRetrievalDTO user1 =
                AccountInfoRetrievalDTO.fromAccountInfo(privateChat.getUser1());
        AccountInfoRetrievalDTO user2 =
                AccountInfoRetrievalDTO.fromAccountInfo(privateChat.getUser2());

        return PrivateChatRetrievalDTO.builder()
                .id(privateChat.getId())
                .user1(user1)
                .user2(user2)
                .build();
    }
}