package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.GroupChatUser.MemberType;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.repositories.GroupChatRepository;
import com.and1ss.onlinechat.repositories.GroupChatUserRepository;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.services.dto.*;
import com.and1ss.onlinechat.services.mappers.AccountInfoMapper;
import com.and1ss.onlinechat.services.mappers.GroupChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.and1ss.onlinechat.utils.DatabaseQueryHelper.*;

@Service
@Transactional
public class GroupChatServiceImpl implements GroupChatService {

    private GroupChatRepository groupChatRepository;

    private GroupChatUserRepository groupChatUserRepository;

    private UserService userService;

    private @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public GroupChatServiceImpl(
            GroupChatRepository groupChatRepository,
            GroupChatUserRepository groupChatUserRepository,
            UserService userService
    ) {
        this.groupChatRepository = groupChatRepository;
        this.groupChatUserRepository = groupChatUserRepository;
        this.userService = userService;
    }

    @Override
    public GroupChatRetrievalDTO createGroupChat(GroupChatCreationDTO chatCreationDTO, UUID creatorId) {
        if (chatCreationDTO.getTitle().isEmpty()) {
            throw new BadRequestException("Group chat must have not empty title");
        }

        if (!chatCreationDTO.getParticipants().contains(creatorId)) {
            chatCreationDTO.getParticipants().add(creatorId);
        }

        if (chatCreationDTO.getParticipants().size() < 2) {
            throw new BadRequestException("Group chat must have at least 2 participants");
        }

        AccountInfo creator = userService.findUserById(creatorId);
        GroupChat toBeCreated = GroupChatMapper.toGroupChat(chatCreationDTO);
        List<AccountInfo> participants = userService.findUsersByListOfIds(chatCreationDTO.getParticipants());

        toBeCreated.setGroupChatUsers(mapToGroupChatUser(participants, toBeCreated));
        toBeCreated.setCreator(creator);

        toBeCreated = groupChatRepository.save(toBeCreated);
        return GroupChatRetrievalDTO.fromGroupChat(toBeCreated, null);
    }

    @Override
    public boolean userMemberOfGroupChat(UUID chatId, UUID userId) {
        AccountInfo user = userService.findUserById(userId);
        GroupChat groupChat = groupChatRepository.findById(chatId).orElseThrow();
        return getGroupChatUser(groupChat, user).isPresent();
    }

    @Override
    public MemberType getUserMemberType(UUID chatId, UUID userId) {
        AccountInfo user = userService.findUserById(userId);
        GroupChat groupChat = groupChatRepository.findById(chatId).orElseThrow();
        return getGroupChatUser(groupChat, user).orElseThrow().getMemberType();
    }

    @Override
    public List<AccountInfoRetrievalDTO> getGroupChatMembers(UUID chatId, UUID authorId) {
        GroupChat groupChat = groupChatRepository.findById(chatId).orElseThrow();

        if (!userMemberOfGroupChat(chatId, authorId)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }
        List<GroupChatUser> joins = groupChatUserRepository.findAllByChatId(groupChat.getId());
        List<UUID> usersIds = joins.stream()
                .map(join -> join.getUser().getId())
                .collect(Collectors.toList());

        return userService.findUsersByListOfIds(usersIds)
                .stream()
                .map(AccountInfoMapper::toAccountInfoRetrievalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> getGroupChatMembersIds(UUID chatId, UUID authorId) {
        return getGroupChatMembers(chatId, authorId)
                .stream()
                .map(AccountInfoRetrievalDTO::getId)
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
                        "       chat_creator.login                         AS chat_creator_login," +
                        "       cast(last_group_message.id AS text)        AS last_message_id, " +
                        "       last_group_message.creation_time           AS last_message_creation_time, " +
                        "       cast(last_group_message.author_id AS text) AS last_message_author_id, " +
                        "       last_group_message.contents                AS last_message_contents, " +
                        "       last_message_author.name                   AS last_message_author_name, " +
                        "       last_message_author.surname                AS last_message_author_surname, " +
                        "       last_message_author.login                  AS last_message_author_login " +
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
    }

    private String getGroupChatByIdQueryString() {
        return getGroupChatQueryString() + "WHERE chat_id = :chat_id";
    }

    // TODO: Refactor this method
    private GroupChatRetrievalDTO mapFromTuple(Tuple tuple) {
        final UUID id = getUUIDFromTupleOrNull(tuple, "chat_id");
        final String title = (String) getFromTupleOrNull(tuple, "chat_title");
        final String about = (String) getFromTupleOrNull(tuple, "chat_about");
        final UUID creatorId = getUUIDFromTupleOrNull(tuple, "chat_creator_id");
        final String creatorName = (String) getFromTupleOrNull(tuple, "chat_creator_name");
        final String creatorSurname = (String) getFromTupleOrNull(tuple, "chat_creator_surname");
        final String creatorLogin = (String) getFromTupleOrNull(tuple, "chat_creator_login");
        final UUID lastMessageId = getUUIDFromTupleOrNull(tuple, "last_message_id");
        final Timestamp lastMessageCreationTime = getTimestampFromTupleOrNull(tuple, "last_message_creation_time");
        final UUID lastMessageAuthorId = getUUIDFromTupleOrNull(tuple, "last_message_author_id");
        final String lastMessageContents = (String) getFromTupleOrNull(tuple, "last_message_contents");
        final String lastMessageAuthorName = (String) getFromTupleOrNull(tuple, "last_message_author_name");
        final String lastMessageAuthorSurname = (String) getFromTupleOrNull(tuple, "last_message_author_surname");
        final String lastMessageAuthorLogin = (String) getFromTupleOrNull(tuple, "last_message_author_login");

        final var groupChatBuilder = GroupChatRetrievalDTO.builder();

        if (id == null || title == null) return null;
        groupChatBuilder.id(id);
        groupChatBuilder.title(title);
        groupChatBuilder.about(about);

        if (creatorId == null || creatorName == null || creatorSurname == null || creatorLogin == null) {
            groupChatBuilder.creator(null);
        } else {
            final var creator = AccountInfoRetrievalDTO.builder()
                    .id(creatorId)
                    .name(creatorName)
                    .surname(creatorSurname)
                    .login(creatorLogin)
                    .build();
            groupChatBuilder.creator(creator);
        }

        if (lastMessageId == null || lastMessageContents == null || lastMessageCreationTime == null) {
            groupChatBuilder.lastMessage(null);
        } else {
            final var lastMessageBuilder = GroupMessageRetrievalDTO.builder();
            lastMessageBuilder.id(lastMessageId);
            lastMessageBuilder.contents(lastMessageContents);
            lastMessageBuilder.chatId(id);
            lastMessageBuilder.createdAt(lastMessageCreationTime);

            if (lastMessageAuthorId == null
                    || lastMessageAuthorName == null
                    || lastMessageAuthorSurname == null
                    || lastMessageAuthorLogin == null
            ) {
                lastMessageBuilder.author(null);
            } else {
                final var lastMessageAuthor = AccountInfoRetrievalDTO.builder()
                        .id(lastMessageAuthorId)
                        .name(lastMessageAuthorName)
                        .surname(lastMessageAuthorSurname)
                        .login(lastMessageAuthorLogin)
                        .build();
                lastMessageBuilder.author(lastMessageAuthor);
            }

            groupChatBuilder.lastMessage(lastMessageBuilder.build());
        }

        return groupChatBuilder.build();
    }

    @Override
    public GroupChatRetrievalDTO getGroupChatById(UUID chatId, UUID authorId) {
        GroupChat groupChat = groupChatRepository.findById(chatId).orElseThrow();
        if (groupChat == null || !userMemberOfGroupChat(chatId, authorId)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        final String queryString = getGroupChatByIdQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("chat_id", chatId);

        return mapFromTuple((Tuple) query.getResultList());
    }

    @Override
    public void patchGroupChat(UUID chatId, GroupChatPatchDTO patchDTO, UUID authorId) {
        GroupChat groupChat = groupChatRepository.findById(chatId).orElseThrow();
        AccountInfo patchAuthor = userService.findUserById(authorId);

        if (patchDTO.getAbout() != null && !patchDTO.getAbout().isEmpty()) {
            groupChat.setAbout(patchDTO.getAbout());
        }

        if (patchDTO.getTitle() != null && !patchDTO.getTitle().isEmpty()) {
            groupChat.setTitle(patchDTO.getTitle());
        }

        if (!userAdminOrCreator(groupChat, patchAuthor)) {
            throw new UnauthorizedException("This user can not patch this chat");
        }

        groupChatRepository.save(groupChat);
    }

    @Override
    public void addUser(UUID chatId, UUID userId, UUID authorId) {
        addUsers(chatId, List.of(userId), authorId);
    }

    @Override
    public void addUsers(UUID chatId, List<UUID> usersIds, UUID authorId) {
        GroupChat chat = groupChatRepository.findGroupChatWithUsersById(chatId).orElseThrow();

        if (!userMemberOfGroupChat(chatId, authorId)) {
            throw new UnauthorizedException("This user cannot add users to this chat");
        }

        List<AccountInfo> users = userService.findUsersByListOfIds(usersIds);
        List<GroupChatUser> newUsers = users.stream().filter(user ->
                !chat.getGroupChatUsers()
                        .stream()
                        .map(GroupChatUser::getUser)
                        .collect(Collectors.toList())
                        .contains(user)
        ).map(user -> new GroupChatUser(chat, user)).collect(Collectors.toList());

        chat.getGroupChatUsers().addAll(newUsers);
        groupChatRepository.save(chat);
    }

    @Override
    public void deleteUser(UUID chatId, UUID userId, UUID authorId) {
        GroupChat chat = groupChatRepository.findGroupChatWithUsersById(chatId).orElseThrow();
        AccountInfo toBeDeleted = userService.findUserById(userId);
        AccountInfo author = userService.findUserById(authorId);
        GroupChatUser toBeDeletedJoin = getGroupChatUser(chat, toBeDeleted).orElseThrow();

        if (!userAdminOrCreator(chat, author)) {
            throw new UnauthorizedException("This user cannot delete members of this chat");
        }

        if (chat.getCreator().equals(toBeDeleted)) {
            throw new UnauthorizedException("This user cannot delete chat creator");
        }

        groupChatUserRepository.delete(toBeDeletedJoin);
    }

    @Override
    public void changeUserMemberType(UUID chatId, UUID userId, UUID authorId, MemberType newMemberType) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    private String getAllGroupChatsWithLastMessageForUserQueryString() {
        return getGroupChatQueryString() +
                "WHERE group_chat.id IN ( " +
                "    SELECT group_chat_id from group_user WHERE user_id = :user_id " +
                ")";
    }

    @Override
    public List<GroupChatRetrievalDTO> getAllGroupChatsForUser(UUID userId) {
        final String queryString = getAllGroupChatsWithLastMessageForUserQueryString();
        final Query query = entityManager.createNativeQuery(queryString, Tuple.class);
        query.setParameter("user_id", userId);

        return ((List<Tuple>) query.getResultList()).stream()
                .map(this::mapFromTuple)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupChatRetrievalDTO> getGroupChatsPageForUser(UUID userId) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    private boolean userAdminOrCreator(GroupChat chat, AccountInfo user) {
        return user != null && (chat.getCreator().equals(user) ||
                (getUserMemberType(chat.getId(), user.getId()) == GroupChatUser.MemberType.admin));
    }

    private Optional<GroupChatUser> getGroupChatUser(GroupChat chat, AccountInfo user) {
        return groupChatUserRepository
                .findByGroupChatIdAndUserId(chat.getId(), user.getId());
    }

    private List<GroupChatUser> mapToGroupChatUser(List<AccountInfo> users, GroupChat groupChat) {
        return users.stream()
                .map(user -> new GroupChatUser(groupChat, user))
                .collect(Collectors.toList());
    }
}
