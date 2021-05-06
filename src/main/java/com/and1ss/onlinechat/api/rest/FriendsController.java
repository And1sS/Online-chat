package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.dto.FriendCreationDTO;
import com.and1ss.onlinechat.services.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.services.FriendsService;
import com.and1ss.onlinechat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        AccountInfo user = userService.authorizeUserByBearerToken(token);
        if (acceptedOnly) {
            return friendsService.getAcceptedFriendsForUser(user.getId());
        } else {
            return friendsService.getFriendsForUser(user.getId());
        }
    }

    @GetMapping("/without-private-chat")
    private List<AccountInfoRetrievalDTO> getFriendsToCreatePrivateChatWith(
            @RequestHeader("Authorization") String token
    ) {
        final AccountInfo user = userService.authorizeUserByBearerToken(token);
        return friendsService.getAcceptedFriendsWithoutPrivateChatsForUser(user.getId());
    }

    @PostMapping
    public FriendRetrievalDTO createFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody FriendCreationDTO friendsDto
    ) {
        AccountInfo user = userService.authorizeUserByBearerToken(token);
        return friendsService.createFriendRequest(user.getId(), friendsDto.getUserId());
    }

    @PutMapping
    public void acceptFriendById(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "user_id") UUID userId
    ) {
        AccountInfo currentUser = userService.authorizeUserByBearerToken(token);
        friendsService.acceptFriendRequest(userId, currentUser.getId());
    }

    @DeleteMapping
    public void deleteFriendById(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "user_id") UUID userId
    ) {
        AccountInfo currentUser = userService.authorizeUserByBearerToken(token);
        friendsService.deleteFriends(currentUser.getId(), userId);
    }
}
