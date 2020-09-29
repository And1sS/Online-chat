package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.services.model.AccountInfo;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoRetrievalDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private String surname;

    public static AccountInfoRetrievalDTO fromAccountInfo(AccountInfo user) {
        return AccountInfoRetrievalDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }
}
