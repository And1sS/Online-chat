package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.api.dto.*;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.PrivateChatService;
import com.and1ss.onlinechat.domain.PrivateChat;
import com.and1ss.onlinechat.repositories.PrivateChatRepository;
import com.and1ss.onlinechat.domain.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.*;
import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.getFromTupleOrNull;

@Service
@Transactional
public class PrivateChatServiceImpl implements PrivateChatService {

    private PrivateChatRepository privateChatRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PrivateChatServiceImpl(PrivateChatRepository privateChatRepository) {
        this.privateChatRepository = privateChatRepository;
    }

    @Override
    public PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author) {
        if (!chat.getUser1().equals(author) && !chat.getUser2().equals(author)) {
            throw new UnauthorizedException("This user can't create this chat");
        }

        PrivateChat privateChat;
        try {
            privateChat = privateChatRepository.save(chat);
        } catch (Exception e) {
            throw new BadRequestException("This chat is already present");
        }

        return privateChat;
    }

    @Override
    public PrivateChat getPrivateChatById(UUID id, AccountInfo author) {
        PrivateChat chat;
        try {
            chat = privateChatRepository.getOne(id);
        } catch (EntityNotFoundException e) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        if (!userMemberOfPrivateChat(chat, author)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        return chat;
    }

    @Override
    public List<PrivateChat> getAllPrivateChatsForUser(AccountInfo user) {
        return privateChatRepository.findPrivateChatsByUserId(user.getId());
    }

    @Override
    public List<PrivateChat> getPrivateChatsPageForUser(AccountInfo user) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public boolean userMemberOfPrivateChat(PrivateChat chat, AccountInfo author) {
        return chat.getUser1().equals(author) || chat.getUser2().equals(author);
    }

    private String getPrivateChatQueryString() {
        return
                "SELECT cast(private_chat.id AS text)              AS private_chat_id, " +
                        "       cast(private_chat.user_1_id AS text)       AS private_chat_user_1_id, " +
                        "       user_1.name                                AS private_chat_user_1_name, " +
                        "       user_1.surname                             AS private_chat_user_1_surname, " +
                        "       user_1.login                               AS private_chat_user_1_login, " +
                        "       cast(private_chat.user_2_id AS text)       AS private_chat_user_2_id, " +
                        "       user_2.name                                AS private_chat_user_2_name, " +
                        "       user_2.surname                             AS private_chat_user_2_surname, " +
                        "       user_2.login                               AS private_chat_user_2_login, " +
                        "       cast(last_private_message.id AS text)      AS last_message_id, " +
                        "       last_private_message.creation_time         AS last_message_creation_time, " +
                        "       cast(last_private_message.user_id AS text) AS last_message_author_id, " +
                        "       last_private_message.contents              AS last_message_contents, " +
                        "       last_message_author.name                   AS last_message_author_name, " +
                        "       last_message_author.surname                AS last_message_author_surname, " +
                        "       last_message_author.login                  AS last_message_author_login " +
                        "FROM private_chat " +
                        "         LEFT OUTER JOIN account_info user_1 ON private_chat.user_1_id = user_1.id " +
                        "         LEFT OUTER JOIN account_info user_2 ON private_chat.user_2_id = user_2.id " +
                        "         LEFT OUTER JOIN ( " +
                        "    SELECT private_message.id, " +
                        "           private_message.chat_id, " +
                        "           private_message.creation_time, " +
                        "           private_message.user_id, " +
                        "           private_message.contents " +
                        "    FROM private_message " +
                        "             INNER JOIN ( " +
                        "        SELECT _message.chat_id            as chat_id, " +
                        "               max(_message.creation_time) as last_message_time " +
                        "        FROM private_message _message " +
                        "        GROUP BY _message.chat_id " +
                        "    ) max_values ON " +
                        "            private_message.chat_id = max_values.chat_id AND " +
                        "            private_message.creation_time = max_values.last_message_time " +
                        "    GROUP BY private_message.chat_id, private_message.id " +
                        ") last_private_message ON private_chat.id = last_private_message.chat_id " +
                        "         LEFT OUTER JOIN account_info last_message_author ON " +
                        "   last_private_message.user_id = last_message_author.id ";
    }

    private String getAllPrivateChatsWithLastMessageForUserQueryString() {
        return getPrivateChatQueryString() +
                " WHERE private_chat.id IN ( " +
                "    SELECT private_chat.id " +
                "    from private_chat " +
                "    WHERE user_1_id = :user_id " +
                "       OR user_2_id = :user_id " +
                ") ";
    }

    private String getPrivateChatByIdQueryString() {
        return getPrivateChatQueryString() +
                " WHERE private_chat.id = :chat_id ";
    }

    @Override
    public PrivateChatRetrievalDTO
    getPrivateChatWithLastMessageDTOById(UUID id, AccountInfo author) {
        final String queryString = getPrivateChatByIdQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("chat_id", id);

        PrivateChatRetrievalDTO result = mapFromTuple((Tuple) query.getResultList());
        if (result == null) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }
        return result;
    }

    @Override
    public List<PrivateChatRetrievalDTO>
    getAllPrivateChatsWithLastMessageDTOForUser(AccountInfo user) {
        final String queryString = getAllPrivateChatsWithLastMessageForUserQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("user_id", user.getId());

        return ((List<Tuple>) query.getResultList()).stream()
                .map(this::mapFromTuple)
                .collect(Collectors.toList());
    }

    // TODO: Refactor this method
    private PrivateChatRetrievalDTO mapFromTuple(Tuple tuple) {
        final UUID id = getUUIDFromTupleOrNull(tuple, "private_chat_id");
        final UUID user1Id = getUUIDFromTupleOrNull(tuple, "private_chat_user_1_id");
        final String user1Name = (String) getFromTupleOrNull(tuple, "private_chat_user_1_name");
        final String user1Surname = (String) getFromTupleOrNull(tuple, "private_chat_user_1_surname");
        final String user1Login = (String) getFromTupleOrNull(tuple, "private_chat_user_1_login");
        final UUID user2Id = getUUIDFromTupleOrNull(tuple, "private_chat_user_2_id");
        final String user2Name = (String) getFromTupleOrNull(tuple, "private_chat_user_2_name");
        final String user2Surname = (String) getFromTupleOrNull(tuple, "private_chat_user_2_surname");
        final String user2Login = (String) getFromTupleOrNull(tuple, "private_chat_user_2_login");
        final UUID lastMessageId = getUUIDFromTupleOrNull(tuple, "last_message_id");
        final Timestamp lastMessageCreationTime = getTimestampFromTupleOrNull(tuple, "last_message_creation_time");
        final UUID lastMessageAuthorId = getUUIDFromTupleOrNull(tuple, "last_message_author_id");
        final String lastMessageContents = (String) getFromTupleOrNull(tuple, "last_message_contents");
        final String lastMessageAuthorName = (String) getFromTupleOrNull(tuple, "last_message_author_name");
        final String lastMessageAuthorSurname = (String) getFromTupleOrNull(tuple, "last_message_author_surname");
        final String lastMessageAuthorLogin = (String) getFromTupleOrNull(tuple, "last_message_author_login");

        final var privateChatBuilder = PrivateChatRetrievalDTO.builder();

        if (id == null) return null;
        privateChatBuilder.id(id);

        if (user1Id == null
                || user1Name == null
                || user1Surname == null
                || user1Login == null) {
            privateChatBuilder.user1(null);
        } else {
            privateChatBuilder.user1(
                    AccountInfoRetrievalDTO.builder()
                            .id(user1Id)
                            .login(user1Login)
                            .name(user1Name)
                            .surname(user1Surname)
                            .build()
            );
        }

        if (user2Id == null
                || user2Name == null
                || user2Surname == null
                || user2Login == null) {
            privateChatBuilder.user2(null);
        } else {
            privateChatBuilder.user2(
                    AccountInfoRetrievalDTO.builder()
                            .id(user2Id)
                            .login(user2Login)
                            .name(user2Name)
                            .surname(user2Surname)
                            .build()
            );
        }

        PrivateMessageRetrievalDTO lastMessage = null;
        if (lastMessageId != null
                && lastMessageContents != null
                && lastMessageCreationTime != null) {

            AccountInfoRetrievalDTO lastMessageAuthor = null;
            if (lastMessageAuthorId != null
                    && lastMessageAuthorName != null
                    && lastMessageAuthorSurname != null
                    && lastMessageAuthorLogin != null) {
                lastMessageAuthor = AccountInfoRetrievalDTO.builder()
                        .id(lastMessageAuthorId)
                        .name(lastMessageAuthorName)
                        .surname(lastMessageAuthorSurname)
                        .login(lastMessageAuthorLogin)
                        .build();
            }
            lastMessage = PrivateMessageRetrievalDTO.builder()
                    .id(lastMessageId)
                    .chatId(id)
                    .contents(lastMessageContents)
                    .createdAt(lastMessageCreationTime)
                    .author(lastMessageAuthor)
                    .build();
        }

        privateChatBuilder.lastMessage(lastMessage);
        return privateChatBuilder.build();
    }
}
