package com.and1ss.onlinechat.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GroupMessagePatchDTO {
    private String contents;

    @JsonProperty("chat_id")
    private UUID chatId;
}
