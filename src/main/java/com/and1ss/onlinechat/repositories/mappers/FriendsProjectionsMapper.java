package com.and1ss.onlinechat.repositories.mappers;

import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.Friends.FriendshipStatus;
import com.and1ss.onlinechat.repositories.projections.FriendsForUserProjection;
import com.and1ss.onlinechat.repositories.projections.FriendsWithoutPrivateChatProjection;

import java.util.UUID;

import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.getEnumFromStringOrNull;
import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.getUUIDFromStringOrNull;

public class FriendsProjectionsMapper {
    public static AccountInfoRetrievalDTO toAccountInfoRetrievalDTOOrNull(
            FriendsWithoutPrivateChatProjection projection
    ) {
        final UUID id = getUUIDFromStringOrNull(projection.getId());
        if (id == null || projection.getLogin() == null
                || projection.getName() == null || projection.getSurname() == null) {
            return null;
        }

        return AccountInfoRetrievalDTO.builder()
                .id(id)
                .name(projection.getName())
                .surname(projection.getSurname())
                .login(projection.getLogin())
                .build();
    }

    public static FriendRetrievalDTO mapToFriendRetrievalOrNull(FriendsForUserProjection projection) {
        final UUID requestIssuerId = getUUIDFromStringOrNull(projection.getRequestIssuerId());
        final UUID requesteeId = getUUIDFromStringOrNull(projection.getRequesteeId());
        final FriendshipStatus status =
                (FriendshipStatus) getEnumFromStringOrNull(projection.getStatus(), FriendshipStatus.class);

        if (requestIssuerId == null || projection.getRequestIssuerLogin() == null
                || projection.getRequestIssuerName() == null || projection.getRequestIssuerSurname() == null
                || requesteeId == null || projection.getRequesteeLogin() == null
                || projection.getRequesteeName() == null || projection.getRequesteeSurname() == null
                || status == null
        ) {
            return null;
        }

        AccountInfoRetrievalDTO requestIssuerDto = new AccountInfoRetrievalDTO(
                requestIssuerId, projection.getRequestIssuerName(),
                projection.getRequestIssuerSurname(), projection.getRequestIssuerLogin());

        AccountInfoRetrievalDTO requesteeDto = new AccountInfoRetrievalDTO(
                requesteeId, projection.getRequesteeName(),
                projection.getRequesteeSurname(), projection.getRequesteeLogin());

        return new FriendRetrievalDTO(requestIssuerDto, requesteeDto, status);
    }
}
