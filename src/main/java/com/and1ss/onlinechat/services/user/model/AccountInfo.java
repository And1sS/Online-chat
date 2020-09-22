package com.and1ss.onlinechat.services.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "account_info")
@DynamicInsert
public class AccountInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id ;

    private String name;
    private String surname;

    @JsonIgnore
    private String login;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at")
    @Generated(GenerationTime.INSERT)
    private Timestamp createdAt;
}
