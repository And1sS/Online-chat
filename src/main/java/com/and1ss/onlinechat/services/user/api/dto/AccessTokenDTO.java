package com.and1ss.onlinechat.services.user.api.dto;

import com.and1ss.onlinechat.services.user.model.AccessToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class AccessTokenDTO {
    @NonNull
    @JsonProperty("access_token")
    private final String accessToken;

    @NonNull
    @JsonProperty("created_at")
    private final Timestamp createdAt;

    public AccessTokenDTO(AccessToken accessToken) {
        this.accessToken = accessToken.getToken();
        createdAt = accessToken.getCreatedAt();
    }
}
