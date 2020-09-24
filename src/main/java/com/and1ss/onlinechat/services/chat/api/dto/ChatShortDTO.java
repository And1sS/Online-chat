package com.and1ss.onlinechat.services.chat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatShortDTO {
    @NonNull
    private final String type;

    @NonNull
    private final UUID id;
}
