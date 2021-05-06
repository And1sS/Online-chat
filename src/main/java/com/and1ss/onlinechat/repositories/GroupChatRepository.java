package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("group_user")
public interface GroupChatRepository extends JpaRepository<GroupChat, UUID> {
    GroupChat findGroupChatById(UUID id);

    @Query(
            name = "from GroupChat gch " +
                    "   join fetch gch.groupChatUsers gchu " +
                    "   join fetch gchu.user " +
                    "where gch.id = :id"
    )
    Optional<GroupChat> findGroupChatWithUsersById(UUID id);

    List<GroupChat> findAllByIdIn(List<UUID> ids);
}
