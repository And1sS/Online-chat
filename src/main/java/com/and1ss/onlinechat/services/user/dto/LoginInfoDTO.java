package com.and1ss.onlinechat.services.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginInfoDTO {
    @NonNull
    @Setter(AccessLevel.NONE)
    private final String login;
    @NonNull
    @Setter(AccessLevel.NONE)
    private final String password;
}
