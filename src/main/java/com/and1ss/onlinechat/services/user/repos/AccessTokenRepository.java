package com.and1ss.onlinechat.services.user.repos;

import com.and1ss.onlinechat.services.user.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
}
