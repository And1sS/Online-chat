package com.and1ss.onlinechat.services.private_chat.model;

import com.and1ss.onlinechat.services.user.model.AccountInfo;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "private_chat")
@RequiredArgsConstructor
@DynamicInsert
public class PrivateChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    protected UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_1_id", referencedColumnName = "id")
    private AccountInfo user1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_2_id", referencedColumnName = "id")
    private AccountInfo user2;

    public PrivateChat(AccountInfo user1, AccountInfo user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
}
