package com.and1ss.onlinechat.api.ws.dto;

import com.and1ss.onlinechat.api.ws.base.WebSocketMessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage<T> {
    @NonNull
    @JsonProperty("message_type")
    private WebSocketMessageType messageType;

    @NonNull
    T payload;
}
