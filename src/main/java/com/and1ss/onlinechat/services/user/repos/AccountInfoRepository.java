package com.and1ss.onlinechat.services.user.repos;

import com.and1ss.onlinechat.services.user.model.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("account_info")
public interface AccountInfoRepository extends JpaRepository<AccountInfo, UUID> {
    AccountInfo findAccountInfoByLogin(String login);
}
