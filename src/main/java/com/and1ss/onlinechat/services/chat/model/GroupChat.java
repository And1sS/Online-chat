package com.and1ss.onlinechat.services.chat.model;

import com.and1ss.onlinechat.services.user.model.AccountInfo;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "group_chat")
@NoArgsConstructor
@DynamicInsert
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NonNull
    private String title;

    private String about;

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private AccountInfo creator;
}