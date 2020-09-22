package com.and1ss.onlinechat.services.user.repos;

import com.and1ss.onlinechat.services.user.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("access_token")
public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
    AccessToken findAccessTokenByUserId(UUID userId);

//    @Query(
//            value = "SELECT * FROM access_token WHERE token=:token",
//            nativeQuery = true
//    )
    AccessToken findAccessTokenByToken(UUID token);
}
