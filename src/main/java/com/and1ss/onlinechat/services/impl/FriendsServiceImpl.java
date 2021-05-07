package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.services.FriendsService;
import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.domain.Friends.FriendshipStatus;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.repositories.FriendsRepository;
import com.and1ss.onlinechat.repositories.mappers.FriendsProjectionsMapper;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.services.mappers.AccountInfoMapper;
import org.hibernate.exception.ConstraintViolationException;
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

    private final UserService userService;

    public FriendsServiceImpl(FriendsRepository friendsRepository, UserService userService) {
        this.friendsRepository = friendsRepository;
        this.userService = userService;
    }

    @Override
    public FriendRetrievalDTO createFriendRequest(UUID requestIssuerId, UUID requesteeId) {
        if (requesteeId.equals(requestIssuerId)) {
            throw new BadRequestException("User can not be a friend of himself");
        }

        if (areUsersFriends(requestIssuerId, requesteeId)) {
            throw new BadRequestException("These users are already friends");
        }

        AccountInfo requestIssuer = userService.findUserById(requestIssuerId);
        AccountInfo requestee = userService.findUserById(requesteeId);

        Friends toCreate = new Friends(requestIssuer, requestee);
        try {
            friendsRepository.save(toCreate);
        } catch (ConstraintViolationException e) {
            throw new BadRequestException("Invalid user id");
        }

        return FriendRetrievalDTO.fromRequestIssuerAndRequesteeAndStatus(
                AccountInfoMapper.toAccountInfoRetrievalDTO(toCreate.getRequestIssuer()),
                AccountInfoMapper.toAccountInfoRetrievalDTO(toCreate.getRequestee()),
                FriendshipStatus.pending
        );
    }

    @Override
    public List<FriendRetrievalDTO> getFriendsForUser(UUID userId) {
        return friendsRepository.getFriendForUser(userId).stream()
                .map(FriendsProjectionsMapper::mapToFriendRetrievalOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRetrievalDTO> getAcceptedFriendsForUser(UUID userId) {
        return friendsRepository.getAcceptedFriendsForUser(userId).stream()
                .map(FriendsProjectionsMapper::mapToFriendRetrievalOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountInfoRetrievalDTO> getAcceptedFriendsWithoutPrivateChatsForUser(UUID userId) {
        return friendsRepository.getAcceptedFriendsWithoutPrivateChatForUser(userId)
                .stream()
                .map(FriendsProjectionsMapper::mapToAccountInfoRetrievalDTOOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean areUsersFriends(UUID user1Id, UUID user2Id) {
        Friends friends = friendsRepository.getFriendsByUsersIds(user1Id, user2Id);

        if (friends == null || friends.getFriendshipStatus() != FriendshipStatus.accepted) {
            return false;
        }
        return true;
    }

    @Override
    public void acceptFriendRequest(UUID requestIssuerId, UUID requesteeId) {
        Friends friends = friendsRepository.getFriendsByUsersIds(requestIssuerId, requesteeId);
        if (friends == null || !friends.getRequestee().getId().equals(requesteeId)
                || !friends.getRequestIssuer().getId().equals(requestIssuerId)) {
            throw new BadRequestException("Invalid friend acceptation request");
        }

        friends.setFriendshipStatus(FriendshipStatus.accepted);
        friendsRepository.save(friends);
    }

    @Override
    public void deleteFriends(UUID user1Id, UUID user2Id) {
        Friends friends = friendsRepository.getFriendsByUsersIds(user1Id, user2Id);

        if (friends == null) {
            throw new BadRequestException("Invalid friend deletion request");
        }
        friendsRepository.delete(friends);
    }
}
