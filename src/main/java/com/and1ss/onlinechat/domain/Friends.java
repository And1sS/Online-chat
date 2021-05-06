package com.and1ss.onlinechat.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "friends")
public class Friends implements Serializable {
    public enum FriendshipStatus { pending, accepted }

    @EmbeddedId
    private FriendsId id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus friendshipStatus;

    public Friends(AccountInfo requestIssuer, AccountInfo requestee) {
        id = new FriendsId(requestIssuer, requestee);
        friendshipStatus = FriendshipStatus.pending;
    }

    public AccountInfo getRequestIssuer() {
        return id.getRequestIssuer();
    }

    public AccountInfo getRequestee() {
        return id.getRequestee();
    }
}
