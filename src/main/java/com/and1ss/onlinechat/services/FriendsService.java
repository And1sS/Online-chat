package com.and1ss.onlinechat.services;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;

import java.util.List;
import java.util.UUID;

public interface FriendsService {
    Friends createFriendRequest(Friends friends, AccountInfo author);
    List<Friends> getFriendsForUser(AccountInfo user);
    Friends isUsersFriends(UUID user1Id, UUID user2Id);
}
