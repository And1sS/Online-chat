package com.and1ss.onlinechat.api.ws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WsGroupMessageDeleteDTO {
    @JsonProperty("chat_id")
    private UUID chatId;

    @JsonProperty("message_id")
    private UUID messageId;
}
