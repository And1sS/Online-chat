package com.and1ss.onlinechat.services.dto;

import com.and1ss.onlinechat.domain.Friends;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FriendRetrievalDTO {
    @JsonProperty("request_issuer")
    private AccountInfoRetrievalDTO requestIssuer;

    @JsonProperty("requestee")
    private AccountInfoRetrievalDTO requestee;

    private Friends.FriendshipStatus status;

    public static FriendRetrievalDTO fromRequestIssuerAndRequesteeAndStatus(
            AccountInfoRetrievalDTO requestIssuer,
            AccountInfoRetrievalDTO requestee,
            Friends.FriendshipStatus status
    ) {
        return builder()
                .requestIssuer(requestIssuer)
                .requestee(requestee)
                .status(status)
                .build();
    }
}
