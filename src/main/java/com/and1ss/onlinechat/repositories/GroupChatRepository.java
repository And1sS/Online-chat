package com.and1ss.onlinechat.repositories;

import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.repositories.projections.GroupChatWithLastMessageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("group_user")
public interface GroupChatRepository extends JpaRepository<GroupChat, UUID> {
    @Query(
            value = "from GroupChat gch " +
                    "   join fetch gch.groupChatUsers gchu " +
                    "   join fetch gchu.id.user " +
                    "where gch.id = :id"
    )
    Optional<GroupChat> findGroupChatWithUsersById(UUID id);

    List<GroupChat> findAllByIdIn(List<UUID> ids);

    @Query(value = getGroupChatsWithLastMessageForUserQuery, nativeQuery = true)
    List<GroupChatWithLastMessageProjection> getGroupChatsWithLastMessageForUser(UUID userId);

    @Query(value = getGroupChatWithLastMessageByIdQuery, nativeQuery = true)
    Optional<GroupChatWithLastMessageProjection> getGroupChatWithLastMessageById(UUID chatId);

    String getGroupChatsWithLastMessageQuery =
            "SELECT DISTINCT cast(group_chat.id AS text)               AS chatId, " +
                    "       group_chat.title                           AS chatTitle, " +
                    "       group_chat.about                           AS chatAbout, " +
                    "       cast(group_chat.creator_id AS text)        AS chatCreatorId, " +
                    "       chat_creator.name                          AS chatCreatorName, " +
                    "       chat_creator.surname                       AS chatCreatorSurname, " +
                    "       chat_creator.login                         AS chatCreatorLogin," +
                    "       cast(last_group_message.id AS text)        AS lastMessageId, " +
                    "       last_group_message.creation_time           AS lastMessageCreationTime, " +
                    "       cast(last_group_message.author_id AS text) AS lastMessageAuthorId, " +
                    "       last_group_message.contents                AS lastMessageContents, " +
                    "       last_message_author.name                   AS lastMessageAuthorName, " +
                    "       last_message_author.surname                AS lastMessageAuthorSurname, " +
                    "       last_message_author.login                  AS lastMessageAuthorLogin " +
                    "FROM group_chat " +
                    "         LEFT OUTER JOIN account_info chat_creator ON group_chat.creator_id = chat_creator" +
                    ".id " +
                    "         LEFT OUTER JOIN ( " +
                    "    SELECT group_message.id, " +
                    "           group_message.chat_id, " +
                    "           group_message.creation_time, " +
                    "           group_message.author_id, " +
                    "           group_message.contents " +
                    "    FROM group_message " +
                    "             INNER JOIN ( " +
                    "        SELECT _message.chat_id            as chat_id, " +
                    "               max(_message.creation_time) as last_message_time " +
                    "        FROM group_message _message " +
                    "        GROUP BY _message.chat_id " +
                    "    ) max_values ON " +
                    "            group_message.chat_id = max_values.chat_id AND " +
                    "            group_message.creation_time = max_values.last_message_time " +
                    "    GROUP BY group_message.chat_id, group_message.id " +
                    ") last_group_message ON group_chat.id = last_group_message.chat_id " +
                    "         LEFT OUTER JOIN account_info last_message_author ON " +
                    "last_message_author.id = last_group_message.author_id ";

    String getGroupChatsWithLastMessageForUserQuery = getGroupChatsWithLastMessageQuery +
            "WHERE group_chat.id IN ( " +
            "    SELECT group_chat_id from group_user WHERE user_id = :userId " +
            ")";

    String getGroupChatWithLastMessageByIdQuery = getGroupChatsWithLastMessageQuery +
            "WHERE group_chat.id = :chatId";

}
