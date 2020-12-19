package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.domain.Friends;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.util.UUID;

@Data
@Builder
public class FriendRetrievalDTO {
    @JsonProperty("request_issuer_id")
    private UUID requestIssuerId;

    @JsonProperty("requestee_id")
    private UUID requesteeId;

    private Friends.FriendshipStatus status;

    public static FriendRetrievalDTO fromFriends(Friends friends) {
        return builder()
                .requestIssuerId(friends.getId().getRequestIssuerId())
                .requesteeId(friends.getId().getRequesteeId())
                .status(friends.getFriendshipStatus())
                .build();
    }
}
