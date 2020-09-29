package com.and1ss.onlinechat.services.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_message")
@DynamicInsert
@DynamicUpdate
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    protected UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private AccountInfo author;

    @JsonIgnore
    @NonNull
    private UUID chatId;

    private String contents;

    @Column(name = "creation_time")
    @Generated(GenerationTime.INSERT)
    private Timestamp createdAt;
}
