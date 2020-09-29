package com.and1ss.onlinechat.services.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
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
@Table(name = "access_token")
@DynamicInsert
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Generated(GenerationTime.INSERT)
    private UUID token;

    @NonNull
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "created_at")
    @Generated(GenerationTime.INSERT)
    private Timestamp createdAt;
}