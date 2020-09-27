package com.and1ss.onlinechat.services.private_chat.model;

import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "private_message")
@DynamicInsert
@DynamicUpdate
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    protected UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AccountInfo author;

    @JsonIgnore
    @NonNull
    private UUID chatId;

    private String contents;

    @Column(name = "creation_time")
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Timestamp createdAt;
}