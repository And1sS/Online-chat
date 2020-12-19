package com.and1ss.onlinechat.api.rest;

import com.and1ss.onlinechat.api.dto.FriendCreationDTO;
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO;
import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.Friends;
import com.and1ss.onlinechat.services.FriendsService;
import com.and1ss.onlinechat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping
    public List<FriendRetrievalDTO> getFriendsForUser(@RequestHeader("Authorization") String token) {
        return getFriendsForUserTransaction(token)
                .stream()
                .map(FriendRetrievalDTO::fromFriends)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Friends> getFriendsForUserTransaction(String token) {
        AccountInfo user = userService.authorizeUserByBearerToken(token);
        return friendsService.getFriendsForUser(user);
    }

    @PostMapping
    public FriendRetrievalDTO createFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody FriendCreationDTO friendsDto
    ) {
        Friends friends = createFriendRequestTransaction(token, friendsDto);
        return FriendRetrievalDTO.fromFriends(friends);
    }

    @Transactional
    public Friends createFriendRequestTransaction(String token, FriendCreationDTO friendsDto) {
        AccountInfo user = userService.authorizeUserByBearerToken(token);
        Friends friends = new Friends(user.getId(), friendsDto.getUserId());
        return friendsService.createFriendRequest(friends, user);
    }
}
