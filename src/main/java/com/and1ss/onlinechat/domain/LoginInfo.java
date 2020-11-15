package com.and1ss.onlinechat.domain;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginInfo {
    @NonNull
    @Setter(AccessLevel.NONE)
    private final String login;
    @NonNull
    @Setter(AccessLevel.NONE)
    private final String password;
}
