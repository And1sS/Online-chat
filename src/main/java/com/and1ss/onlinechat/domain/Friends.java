package com.and1ss.onlinechat.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "friends")
public class Friends {
    public enum FriendshipStatus { pending, accepted }

    @EmbeddedId
    private FriendsId id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus friendshipStatus;

    public Friends(UUID requestIssuerId, UUID requesteeId) {
        FriendsId id = new FriendsId(requestIssuerId, requesteeId);

        this.id = id;
        friendshipStatus = FriendshipStatus.pending;
    }
}
