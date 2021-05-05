package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.domain.Friends.FriendshipStatus;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.repositories.FriendsRepository;
import com.and1ss.onlinechat.repositories.mappers.FriendsProjectionsMapper;
import com.and1ss.onlinechat.services.FriendsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendsServiceImpl implements FriendsService {

    private final FriendsRepository friendsRepository;

    public FriendsServiceImpl(FriendsRepository friendsRepository) {
        this.friendsRepository = friendsRepository;
    }

    @Override
    public Friends createFriendRequest(Friends friends, AccountInfo author) {
        if (!friends.getId().getRequestIssuerId().equals(author.getId())) {
            throw new UnauthorizedException("This user can not create friends request");
        }

        Friends usersFriends = getFriendsByUsersIds(
                friends.getId().getRequesteeId(),
                friends.getId().getRequestIssuerId()
        );

        if (usersFriends != null) {
            throw new BadRequestException("This users are already friends");
        }

        return friendsRepository.save(friends);
    }

    @Override
    public List<FriendRetrievalDTO> getFriendsForUser(AccountInfo user) {
        return friendsRepository.getFriendForUser(user.getId()).stream()
                .map(FriendsProjectionsMapper::mapToFriendRetrievalOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRetrievalDTO> getAcceptedFriendsForUser(AccountInfo user) {
        return friendsRepository.getAcceptedFriendsForUser(user.getId()).stream()
                .map(FriendsProjectionsMapper::mapToFriendRetrievalOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountInfoRetrievalDTO> getAcceptedFriendsWithoutPrivateChatsForUser(AccountInfo user) {
        return friendsRepository.getAcceptedFriendsWithoutPrivateChatForUser(user.getId())
                .stream()
                .map(FriendsProjectionsMapper::mapToAccountInfoRetrievalDTOOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Friends getFriendsByUsersIds(UUID user1Id, UUID user2Id) {
        return friendsRepository.getFriendsById(user1Id, user2Id);
    }

    @Override
    public void acceptFriendRequest(AccountInfo user, Friends friends) {
        if (!user.getId().equals(friends.getId().getRequesteeId()) &&
                !user.getId().equals(friends.getId().getRequestIssuerId())
        ) {
            throw new UnauthorizedException("This user can not accept this invitation");
        }

        friends.setFriendshipStatus(FriendshipStatus.accepted);
        friendsRepository.save(friends);
    }

    @Override
    public void deleteFriends(AccountInfo user, Friends friends) {
        if (!user.getId().equals(friends.getId().getRequesteeId()) &&
                !user.getId().equals(friends.getId().getRequestIssuerId())
        ) {
            throw new UnauthorizedException("This user can not delete this friends");
        }

        friendsRepository.delete(friends);
    }
}
