package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;

import java.util.List;
import java.util.UUID;

public interface FriendsService {
    Friends createFriendRequest(Friends friends, AccountInfo author);
    List<FriendRetrievalDTO> getFriendsForUserDTO(AccountInfo user);
    List<FriendRetrievalDTO> getAcceptedFriendsForUserDTO(AccountInfo user);
    List<AccountInfoRetrievalDTO> getAcceptedFriendsWithoutPrivateChatsForUserDTO(AccountInfo user);
    Friends getFriendsByUsersIds(UUID user1Id, UUID user2Id);
    void acceptFriendRequest(AccountInfo user, Friends friends);
    void deleteFriends(AccountInfo user, Friends friends);
}
