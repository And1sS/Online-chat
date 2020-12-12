package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.api.dto.GroupChatRetrievalDTO;
import com.and1ss.onlinechat.api.dto.GroupMessageRetrievalDTO;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.GroupChatUserId;
import com.and1ss.onlinechat.repositories.GroupChatRepository;
import com.and1ss.onlinechat.repositories.GroupChatUserRepository;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.domain.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.*;

@Service
@Transactional
public class GroupChatServiceImpl implements GroupChatService {

    private GroupChatRepository groupChatRepository;

    private GroupChatUserRepository groupChatUserJoinRepository;

    private UserService userService;

    private @PersistenceContext EntityManager entityManager;

    @Autowired
    public GroupChatServiceImpl(
            GroupChatRepository groupChatRepository,
            GroupChatUserRepository groupChatUserJoinRepository,
            UserService userService
    ) {
        this.groupChatRepository = groupChatRepository;
        this.groupChatUserJoinRepository = groupChatUserJoinRepository;
        this.userService = userService;
    }

    @Override
    public GroupChat createGroupChat(
            GroupChat chat,
            List<AccountInfo> participants,
            AccountInfo author
    ) {
        if (chat.getTitle().isEmpty()) {
            throw new BadRequestException("Group chat must have not empty title");
        }

        if (!participants.contains(author)) {
            participants.add(author);
        }

        GroupChat createdChat;
        try {
            createdChat = groupChatRepository.save(chat);
        } catch (Exception e) {
            throw new BadRequestException("This chat is already present");
        }

        uncheckedAddUsers(chat, author, participants);

        return createdChat;
    }

    @Override
    public boolean userMemberOfGroupChat(GroupChat chat, AccountInfo user) {
        return getGroupChatUserJoin(chat, user) != null;
    }

    @Override
    public GroupChatUser.MemberType getUserMemberType(GroupChat chat, AccountInfo user) {
        GroupChatUser join = getGroupChatUserJoin(chat, user);

        if (join == null) {
            throw new BadRequestException("This user is not member of this chat");
        }

        return join.getMemberType();
    }

    private boolean userAdminOrCreator(GroupChat chat, AccountInfo user) {
        return chat.getCreator().equals(user) ||
                (getGroupChatUserJoin(chat, user).getMemberType()
                        == GroupChatUser.MemberType.admin);
    }

    private GroupChatUser getGroupChatUserJoin(GroupChat chat, AccountInfo user) {
        return groupChatUserJoinRepository
                .findByGroupChatIdAndUserId(chat.getId(), user.getId());
    }

    @Override
    public List<AccountInfo> getGroupChatMembers(GroupChat chat, AccountInfo author) {
        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }
        List<GroupChatUser> joins = groupChatUserJoinRepository.findAllByChatId(chat.getId());
        List<UUID> usersIds = joins.stream()
                .map(join -> join.getId().getUserId())
                .collect(Collectors.toList());

        return userService.findUsersByListOfIds(usersIds);
    }

    @Override
    public List<UUID> getGroupChatMembersIds(GroupChat chat, AccountInfo author) {
        return getGroupChatMembers(chat, author)
                .stream()
                .map(AccountInfo::getId)
                .collect(Collectors.toList());
    }

    private String getGroupChatQueryString() {
        return
            "SELECT cast(group_chat.id AS text)                AS chat_id, " +
            "       group_chat.title                           AS chat_title, " +
            "       group_chat.about                           AS chat_about, " +
            "       cast(group_chat.creator_id AS text)        AS chat_creator_id, " +
            "       chat_creator.name                          AS chat_creator_name, " +
            "       chat_creator.surname                       AS chat_creator_surname, " +
            "       cast(last_group_message.id AS text)        AS last_message_id, " +
            "       last_group_message.creation_time           AS last_message_creation_time, " +
            "       cast(last_group_message.author_id AS text) AS last_message_author_id, " +
            "       last_group_message.contents                AS last_message_contents, " +
            "       last_message_author.name                   AS last_message_author_name, " +
            "       last_message_author.surname                AS last_message_author_surname " +
            "FROM group_chat " +
            "         LEFT OUTER JOIN account_info chat_creator ON group_chat.creator_id = chat_creator.id " +
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
    }

    private String getGroupChatByIdQueryString() {
        return getGroupChatQueryString() + "WHERE chat_id = :chat_id";
    }

    private GroupChatRetrievalDTO mapFromTuple(Tuple tuple) {
        final UUID id = getUUIDFromTupleOrNull(tuple, "chat_id");
        final String title = (String) getFromTupleOrNull(tuple, "chat_title");
        final String about = (String) getFromTupleOrNull(tuple, "chat_about");
        final UUID creatorId = getUUIDFromTupleOrNull(tuple, "chat_creator_id");
        final String creatorName = (String) getFromTupleOrNull(tuple, "chat_creator_name");
        final String creatorSurname = (String) getFromTupleOrNull(tuple, "chat_creator_surname");
        final UUID lastMessageId = getUUIDFromTupleOrNull(tuple, "last_message_id");
        final Timestamp lastMessageCreationTime = getTimestampFromTupleOrNull(tuple, "last_message_creation_time");
        final UUID lastMessageAuthorId = getUUIDFromTupleOrNull(tuple, "last_message_author_id");
        final String lastMessageContents = (String) getFromTupleOrNull(tuple, "last_message_contents");
        final String lastMessageAuthorName = (String) getFromTupleOrNull(tuple, "last_message_author_name");
        final String lastMessageAuthorSurname = (String) getFromTupleOrNull(tuple, "last_message_author_surname");

        final var groupChatBuilder = GroupChatRetrievalDTO.builder();

        if (id == null || title == null) return null;
        groupChatBuilder.id(id);
        groupChatBuilder.title(title);
        groupChatBuilder.about(about);

        if (creatorId == null || creatorName == null || creatorSurname == null) {
            groupChatBuilder.creator(null);
        } else {
            final var creator = AccountInfoRetrievalDTO.builder()
                    .id(creatorId)
                    .name(creatorName)
                    .surname(creatorSurname)
                    .build();
            groupChatBuilder.creator(creator);
        }

        if (lastMessageId == null ||
                lastMessageContents == null ||
                lastMessageCreationTime == null) {
            groupChatBuilder.lastMessage(null);
        } else {
            final var lastMessageBuilder = GroupMessageRetrievalDTO.builder();
            lastMessageBuilder.id(lastMessageId);
            lastMessageBuilder.contents(lastMessageContents);
            lastMessageBuilder.chatId(id);
            lastMessageBuilder.createdAt(lastMessageCreationTime);

            if (lastMessageAuthorId == null
                    || lastMessageAuthorName == null
                    || lastMessageAuthorSurname == null) {
                lastMessageBuilder.author(null);
            } else {
                final var lastMessageAuthor = AccountInfoRetrievalDTO.builder()
                        .id(lastMessageAuthorId)
                        .name(lastMessageAuthorName)
                        .surname(lastMessageAuthorSurname)
                        .build();
                lastMessageBuilder.author(lastMessageAuthor);
            }

            groupChatBuilder.lastMessage(lastMessageBuilder.build());
        }

        return groupChatBuilder.build();
    }

    @Override
    public GroupChat getGroupChatById(UUID id, AccountInfo author) {
        GroupChat chat;
        try {
            chat = groupChatRepository.getOne(id);
        } catch (Exception e) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        return chat;
    }

    @Override
    public GroupChatRetrievalDTO getGroupChatDTOById(UUID id, AccountInfo author) {
        final String queryString = getGroupChatByIdQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("chat_id", id);

        return mapFromTuple((Tuple) query.getResultList());
    }

    @Override
    public void patchGroupChat(GroupChat chat, AccountInfo author) {
        if (!userAdminOrCreator(chat, author)) {
            throw new UnauthorizedException("This user can not patch this chat");
        }

        groupChatRepository.save(chat);
    }

    @Override
    public void addUser(GroupChat chat, AccountInfo author, AccountInfo toBeAdded) {
        if (groupChatRepository.findGroupChatById(chat.getId()) == null) {
            throw new BadRequestException("This chat does not exist");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        if (!userMemberOfGroupChat(chat, toBeAdded)) {
            GroupChatUserId compositeId = new GroupChatUserId(
                    chat.getId(), toBeAdded.getId());

            GroupChatUser join = GroupChatUser.builder()
                    .memberType(GroupChatUser.MemberType.readwrite)
                    .id(compositeId)
                    .build();

            groupChatUserJoinRepository.save(join);
        } else {
            throw new BadRequestException("This user is already member of this chat");
        }
    }

    @Override
    public void addUsers(GroupChat chat, AccountInfo author, List<AccountInfo> toBeAdded) {
        if (groupChatRepository.findGroupChatById(chat.getId()) == null) {
            throw new BadRequestException("This chat does not exist");
        }

        if (!userMemberOfGroupChat(chat, author)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        uncheckedAddUsers(chat, author, toBeAdded);
    }

    // TODO: Now, this method assumes that author is chat creator
    // Fix this
    private void uncheckedAddUsers(
            GroupChat chat,
            AccountInfo author,
            List<AccountInfo> toBeAdded
    ) {
        Set<GroupChatUser> allUsersJoin = toBeAdded.stream()
                .filter(user -> !userMemberOfGroupChat(chat, user))
                .map(user -> {
                    GroupChatUser.MemberType memberType = GroupChatUser.MemberType.readwrite;
                    if (user.equals(author)) {
                        memberType = GroupChatUser.MemberType.admin;
                    }

                    return GroupChatUser.builder()
                            .id(new GroupChatUserId(chat.getId(), user.getId()))
                            .memberType(memberType)
                            .build();
                }).collect(Collectors.toSet());

        groupChatUserJoinRepository.saveAll(allUsersJoin);
    }

    @Override
    public void deleteUser(GroupChat chat, AccountInfo author, AccountInfo toBeDeleted) {
        GroupChatUser toBeDeletedJoin = getGroupChatUserJoin(chat, toBeDeleted);

        if (!userAdminOrCreator(chat, author)) {
            throw new UnauthorizedException("This user cannot delete members of this chat");
        }

        if (chat.getCreator().equals(toBeDeleted)) {
            throw new UnauthorizedException("This user cannot delete chat creator");
        }

        groupChatUserJoinRepository.delete(toBeDeletedJoin);
    }

    @Override
    public void changeUserMemberType(
            GroupChat chat,
            AccountInfo author,
            AccountInfo member,
            GroupChatUser.MemberType newMemberType
    ) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    private String getAllGroupChatsForUserQueryString() {
        return getGroupChatQueryString() +
            "WHERE group_chat.id IN ( " +
            "    SELECT group_chat_id from group_user WHERE user_id = :user_id " +
            ")";
    }

    @Override
    public List<GroupChatRetrievalDTO> getAllGroupChatsDTOForUser(AccountInfo user) {
        final String queryString = getAllGroupChatsForUserQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("user_id", user.getId());

        return ((List<Tuple>) query.getResultList()).stream()
                .map(this::mapFromTuple)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupChatRetrievalDTO> getGroupChatsPageForUser(AccountInfo user) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }
}
