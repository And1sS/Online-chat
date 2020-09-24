package com.and1ss.onlinechat.services.chat.api.dto;

import com.and1ss.onlinechat.services.chat.model.group_chat.GroupChat;
import com.and1ss.onlinechat.services.chat.model.private_chat.PrivateChat;
import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatFullDTO {
    @NonNull
    @JsonProperty("type")
    private String type;

    private String title;
    private String about;

    @JsonProperty("creator_id")
    private UUID creator;

    @NonNull
    @JsonProperty("user_ids")
    private List<UUID> users;

    public ChatFullDTO(PrivateChat privateChat) {
        this.type = ChatType.CHAT_PRIVATE;
        this.users = Arrays.asList(
                privateChat.getUser1().getId(),
                privateChat.getUser2().getId()
        );
    }

    public ChatFullDTO(GroupChat groupChat, List<AccountInfo> participants) {
        this.type = ChatType.CHAT_GROUP;
        this.title = groupChat.getTitle();
        this.creator = groupChat.getCreator().getId();
        this.about = groupChat.getAbout();
        this.users = participants.stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());
    }
}
