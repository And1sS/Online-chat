package com.and1ss.onlinechat.services.chat.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatDTO {
    public static final String CHAT_PRIVATE = "private";
    public static final String CHAT_GROUP = "group";

    @NonNull
    @JsonProperty("type")
    private final String type;

    private final String title;
    private final String about;

    @NonNull
    @JsonProperty("users")
    private final List<UUID> users;
}
