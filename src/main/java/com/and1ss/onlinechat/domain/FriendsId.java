package com.and1ss.onlinechat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.PostgresUUIDType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class FriendsId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "request_issuer_id", referencedColumnName = "id",
            insertable = false, updatable = false
    )
    private AccountInfo requestIssuer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "requestee_id", referencedColumnName = "id",
            insertable = false, updatable = false
    )
    private AccountInfo requestee;
}