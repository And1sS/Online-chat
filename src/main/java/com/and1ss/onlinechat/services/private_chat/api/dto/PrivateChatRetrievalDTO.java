package com.and1ss.onlinechat.services.private_chat.api.dto;

import com.and1ss.onlinechat.services.group_chat.model.GroupChat;
import com.and1ss.onlinechat.services.private_chat.model.PrivateChat;
import com.and1ss.onlinechat.services.user.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .user1(user1)
                .user2(user2)
                .build();
    }
}