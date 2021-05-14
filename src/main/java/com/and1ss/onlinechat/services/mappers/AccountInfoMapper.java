package com.and1ss.onlinechat.services.mappers;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;

public class AccountInfoMapper {
    public static AccountInfoRetrievalDTO toAccountInfoRetrievalDTO(AccountInfo user) {
            return AccountInfoRetrievalDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .login(user.getLogin())
                    .build();
    }
}
