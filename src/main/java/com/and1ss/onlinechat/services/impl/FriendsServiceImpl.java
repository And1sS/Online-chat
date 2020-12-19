package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.repositories.FriendsRepository;
import com.and1ss.onlinechat.services.FriendsService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FriendsServiceImpl implements FriendsService {

    private final FriendsRepository friendsRepository;

    private @PersistenceContext EntityManager entityManager;

    public FriendsServiceImpl(FriendsRepository friendsRepository) {
        this.friendsRepository = friendsRepository;
    }

    @Override
    public Friends createFriendRequest(Friends friends, AccountInfo author) {
        Friends usersFriends = isUsersFriends(
                friends.getId().getRequesteeId(),
                friends.getId().getRequestIssuerId()
        );
        if (usersFriends != null) {
            throw new BadRequestException("This users are already friends");
        }

        return friendsRepository.save(friends);
    }

    @Override
    public List<Friends> getFriendsForUser(AccountInfo user) {
        return friendsRepository.getFriendsByUserId(user.getId());
    }

    @Override
    public Friends isUsersFriends(UUID user1Id, UUID user2Id) {
        return friendsRepository.getFriendsById(user1Id, user2Id);
    }
}
