package com.and1ss.onlinechat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.PostgresUUIDType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class FriendsId implements Serializable {
    @Column(columnDefinition = "request_issuer_id")
    private UUID requestIssuerId;

    @Column(columnDefinition = "requestee_id")
    private UUID requesteeId;
}