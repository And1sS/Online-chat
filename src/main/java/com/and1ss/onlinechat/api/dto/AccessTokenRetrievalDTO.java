package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.domain.AccessToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccessTokenRetrievalDTO {
    @NonNull
    @JsonProperty("access_token")
    private final UUID accessToken;

    @NonNull
    @JsonProperty("created_at")
    private final Timestamp createdAt;

    public AccessTokenRetrievalDTO(AccessToken accessToken) {
        this.accessToken = accessToken.getToken();
        createdAt = accessToken.getCreatedAt();
    }
}
