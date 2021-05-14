package com.and1ss.onlinechat.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Table(name = "group_user")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatUser {
    public enum MemberType { read, readwrite, admin }

    @AttributeOverrides({
            @AttributeOverride(name="groupChatId", column = @Column(name="group_chat_id")),
            @AttributeOverride(name="userId", column = @Column(name="user_id"))
    })
    @EmbeddedId
    private GroupChatUserId id;

    @MapsId("groupChatId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id", referencedColumnName = "id")
    private GroupChat groupChat;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AccountInfo user;

    @NonNull
    @Column(name = "member_type")
    @Enumerated(EnumType.STRING)
    private MemberType memberType = MemberType.readwrite;

    public GroupChatUser(GroupChat groupChat, AccountInfo user) {
        this.groupChat = groupChat;
        this.user = user;

        this.id = new GroupChatUserId(groupChat.getId(), user.getId());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class GroupChatUserId implements Serializable {
        private UUID groupChatId;

        private UUID userId;
    }
}
