package com.and1ss.onlinechat.api;

import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.api.dto.AccessTokenRetrievalDTO;
import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.model.LoginInfo;
import com.and1ss.onlinechat.services.model.RegisterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-service/auth")
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService service) {
        userService = service;
    }

    @PostMapping("/register")
    private AccountInfoRetrievalDTO
    registerUser(@RequestBody RegisterInfo registerInfo) {
        return AccountInfoRetrievalDTO
                .fromAccountInfo(userService.registerUser(registerInfo));
    }

    @GetMapping("/login")
    private AccessTokenRetrievalDTO loginUser(@RequestBody LoginInfo credentials) {
        return new AccessTokenRetrievalDTO(userService.loginUser(credentials));
    }
}
