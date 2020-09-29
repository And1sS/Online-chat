package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.services.group_chat.model.GroupChat;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @NonNull
    private List<AccountInfoRetrievalDTO> participants;

    public static GroupChatRetrievalDTO fromGroupChat(
            GroupChat groupChat,
            List<AccountInfo> participants
    ) {
        List<AccountInfoRetrievalDTO> participantsRetrieval =
                participants.stream()
                        .map(AccountInfoRetrievalDTO::fromAccountInfo)
                        .collect(Collectors.toList());

        AccountInfoRetrievalDTO creatorDTO =
                AccountInfoRetrievalDTO.fromAccountInfo(groupChat.getCreator());

        return GroupChatRetrievalDTO.builder()
                .title(groupChat.getTitle())
                .id(groupChat.getId())
                .about(groupChat.getAbout())
                .creator(creatorDTO)
                .participants(participantsRetrieval)
                .build();
    }
}

