package com.and1ss.onlinechat.services.impl.repos;

import com.and1ss.onlinechat.services.model.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("account_info")
public interface AccountInfoRepository extends JpaRepository<AccountInfo, UUID> {
    AccountInfo findAccountInfoByLogin(String login);
    AccountInfo findAccountInfoById(UUID id);
    List<AccountInfo> findAllByIdIn(List<UUID> uuids);
}
