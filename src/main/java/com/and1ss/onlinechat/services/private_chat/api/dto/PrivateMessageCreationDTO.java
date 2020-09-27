package com.and1ss.onlinechat.services.private_chat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessageCreationDTO {
    @NonNull
    private String contents;
}
