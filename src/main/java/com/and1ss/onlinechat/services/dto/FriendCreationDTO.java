package com.and1ss.onlinechat.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class FriendCreationDTO {
    @JsonProperty("user_id")
    private UUID userId;
}
