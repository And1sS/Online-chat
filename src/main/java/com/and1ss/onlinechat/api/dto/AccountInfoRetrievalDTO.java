package com.and1ss.onlinechat.api.dto;

import com.and1ss.onlinechat.domain.AccountInfo;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoRetrievalDTO {
    @NonNull
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private String surname;

    @NonNull
    private String login;

    public static AccountInfoRetrievalDTO fromAccountInfo(AccountInfo user) {
        return AccountInfoRetrievalDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .login(user.getLogin())
                .build();
    }
}
