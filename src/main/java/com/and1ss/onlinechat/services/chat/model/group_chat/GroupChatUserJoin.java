package com.and1ss.onlinechat.services.chat.model.group_chat;

import lombok.*;

import javax.persistence.*;

@Data
@Table(name = "group_user")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatUserJoin {
    public enum MemberType { read, readwrite, admin }

    @EmbeddedId
    GroupChatUserJoinId id;

    @NonNull
    @Column(name = "member_type")
    @Enumerated(EnumType.STRING)
    private MemberType memberType;
}
