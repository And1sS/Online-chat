package com.and1ss.onlinechat.services.user.api.dto;

import com.and1ss.onlinechat.services.user.model.AccountInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@Data
public class AccountInfoDTO {
    @NonNull
    private final UUID id;

    @NonNull
    private final String name;

    @NonNull
    private final String surname;

    @NonNull
    private final String login;

    @NonNull
    @JsonProperty("created_at")
    private final Timestamp createdAt;

    public AccountInfoDTO(AccountInfo accountInfo) {
        id = accountInfo.getId();
        name = accountInfo.getName();
        surname = accountInfo.getSurname();
        login = accountInfo.getLogin();
        createdAt = accountInfo.getCreatedAt();
    }
}
