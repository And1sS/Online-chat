package com.and1ss.onlinechat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class GroupChatUserId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "group_chat_id", referencedColumnName = "id",
            insertable = false, updatable = false
    )
    private GroupChat groupChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id", referencedColumnName = "id",
            insertable = false, updatable = false
    )
    private AccountInfo user;
}
