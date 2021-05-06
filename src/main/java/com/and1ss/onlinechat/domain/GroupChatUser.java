package com.and1ss.onlinechat.domain;

import lombok.*;

import javax.persistence.*;

@Data
@Table(name = "group_user")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatUser {
    public enum MemberType { read, readwrite, admin }

    @EmbeddedId
    private GroupChatUserId id;

    @NonNull
    @Column(name = "member_type")
    @Enumerated(EnumType.STRING)
    private MemberType memberType = MemberType.readwrite;

    public GroupChatUser(GroupChat groupChat, AccountInfo user) {
        this.id = new GroupChatUserId(groupChat, user);
    }

    public AccountInfo getUser() {
        return id.getUser();
    }

    public GroupChat getGroupChat() {
        return id.getGroupChat();
    }
}
