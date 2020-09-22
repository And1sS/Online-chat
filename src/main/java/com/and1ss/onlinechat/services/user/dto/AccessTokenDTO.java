package com.and1ss.onlinechat.services.user.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AccessTokenDTO {
    @NonNull
    @Setter(AccessLevel.NONE)
    private final UUID id;
    @NonNull
    @Setter(AccessLevel.NONE)
    private final String token;
}
