package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.api.dto.FriendCreationDTO;
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.services.FriendsService;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.utils.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-service/friends")
public class FriendsController {
    private final FriendsService friendsService;

    private final UserService userService;

    @Autowired
    public FriendsController(FriendsService friendsService, UserService userService) {
        this.friendsService = friendsService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<FriendRetrievalDTO> getFriendsForUser(
            @RequestHeader("Authorization") String token,
            @RequestParam(
                    value = "accepted_only",
                    required = false,
                    defaultValue = "false"
            ) Boolean acceptedOnly
    ) {
        return getFriendsForUserTransaction(token, acceptedOnly);
    }

    @Transactional
    public List<FriendRetrievalDTO> getFriendsForUserTransaction(String token, boolean acceptedOnly) {
        AccountInfo user = userService.authorizeUserByBearerToken(token);
        if (acceptedOnly) {
            return friendsService.getAcceptedFriendsForUserDTO(user);
        } else {
            return friendsService.getFriendsForUserDTO(user);
        }
    }

    @PostMapping
    public FriendRetrievalDTO createFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody FriendCreationDTO friendsDto
    ) {
        Triple<Friends, AccountInfo, AccountInfo> created = createFriendRequestTransaction(token, friendsDto);
        AccountInfoRetrievalDTO requestIssuerDto = AccountInfoRetrievalDTO.fromAccountInfo(created.getSecond());
        AccountInfoRetrievalDTO requesteeDto = AccountInfoRetrievalDTO.fromAccountInfo(created.getThird());
        Friends.FriendshipStatus status = created.getFirst().getFriendshipStatus();

        return FriendRetrievalDTO.fromRequestIssuerAndRequesteeAndStatus(
                requestIssuerDto, requesteeDto, status
        );
    }

    @Transactional
    public Triple<Friends, AccountInfo, AccountInfo> createFriendRequestTransaction(
            String token, FriendCreationDTO friendsDto) {
        AccountInfo user = userService.authorizeUserByBearerToken(token);
        Friends friends = new Friends(user.getId(), friendsDto.getUserId());
        Friends createdFriends = friendsService.createFriendRequest(friends, user);
        AccountInfo requestee = userService.findUserById(friendsDto.getUserId());

        return new Triple(createdFriends, user, requestee);
    }

    @PutMapping
    public void acceptFriendById(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "user_id") UUID userId
    ) {
        acceptFriendByIdTransaction(token, userId);
    }

    @Transactional
    public void acceptFriendByIdTransaction(String token, UUID otherUserId) {
        AccountInfo currentUser = userService.authorizeUserByBearerToken(token);
        Friends friends = friendsService.getFriendsByUsersIds(currentUser.getId(), otherUserId);

        if (friends == null) {
            throw new BadRequestException("Invalid friend id");
        }
        friendsService.acceptFriendRequest(currentUser, friends);
    }

    @DeleteMapping
    public void deleteFriendById(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "user_id") UUID userId
    ) {
        deleteFriendByIdTransaction(token, userId);
    }

    @Transactional
    public void deleteFriendByIdTransaction(String token, UUID otherUserId) {
        AccountInfo currentUser = userService.authorizeUserByBearerToken(token);
        Friends friends = friendsService.getFriendsByUsersIds(currentUser.getId(), otherUserId);

        if (friends == null) {
            throw new BadRequestException("Invalid friend id");
        }
        friendsService.deleteFriends(currentUser, friends);
    }
}
