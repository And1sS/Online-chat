package com.and1ss.onlinechat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatCreationDTO {
    @NonNull
    private String title;

    private String about;

    @NonNull
    private List<UUID> participants;
}
