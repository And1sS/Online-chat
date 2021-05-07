package com.and1ss.onlinechat.services.impl;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.domain.GroupChat;
import com.and1ss.onlinechat.domain.GroupChatUser;
import com.and1ss.onlinechat.domain.GroupChatUser.MemberType;
import com.and1ss.onlinechat.exceptions.BadRequestException;
import com.and1ss.onlinechat.exceptions.UnauthorizedException;
import com.and1ss.onlinechat.repositories.GroupChatRepository;
import com.and1ss.onlinechat.repositories.GroupChatUserRepository;
import com.and1ss.onlinechat.repositories.mappers.GroupChatProjectionsMapper;
import com.and1ss.onlinechat.repositories.projections.GroupChatWithLastMessageProjection;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import com.and1ss.onlinechat.services.dto.AccountInfoRetrievalDTO;
import com.and1ss.onlinechat.services.dto.GroupChatCreationDTO;
import com.and1ss.onlinechat.services.dto.GroupChatPatchDTO;
import com.and1ss.onlinechat.services.dto.GroupChatRetrievalDTO;
import com.and1ss.onlinechat.services.mappers.AccountInfoMapper;
import com.and1ss.onlinechat.services.mappers.GroupChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupChatServiceImpl implements GroupChatService {

    private GroupChatRepository groupChatRepository;

    private GroupChatUserRepository groupChatUserRepository;

    private UserService userService;

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
        return GroupChatMapper.toGroupChatRetrievalDTO(toBeCreated);
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

        List<UUID> usersIds = groupChatUserRepository.findAllByChatId(groupChat.getId())
                .stream()
                .map(groupChatUser -> groupChatUser.getUser().getId())
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

    @Override
    public GroupChatRetrievalDTO getGroupChatById(UUID chatId, UUID authorId) {
        if (!userMemberOfGroupChat(chatId, authorId)) {
            throw new UnauthorizedException("This user is not allowed to view this chat");
        }

        GroupChatWithLastMessageProjection projection = groupChatRepository
                .getGroupChatWithLastMessageById(chatId)
                .orElseThrow();
        return GroupChatProjectionsMapper.toGroupChatRetrievalDTO(projection);
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
        GroupChat chat = groupChatRepository
                .findGroupChatWithUsersById(chatId)
                .orElseThrow();

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
        GroupChat chat = groupChatRepository
                .findGroupChatWithUsersById(chatId)
                .orElseThrow();
        AccountInfo toBeDeleted = userService.findUserById(userId);
        AccountInfo author = userService.findUserById(authorId);
        GroupChatUser toBeDeletedJoin = getGroupChatUser(chat, toBeDeleted).orElseThrow();

        if (!userAdminOrCreator(chat, author)) {
            throw new UnauthorizedException("This user cannot delete members of this chat");
        }

        if (chat.getCreator().equals(toBeDeleted)) {
            throw new UnauthorizedException("This user cannot delete chat creator");
        }

        chat.getGroupChatUsers().remove(toBeDeletedJoin);
        groupChatUserRepository.delete(toBeDeletedJoin);
    }

    @Override
    public void changeUserMemberType(UUID chatId, UUID userId, UUID authorId, MemberType newMemberType) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public List<GroupChatRetrievalDTO> getAllGroupChatsForUser(UUID userId) {
        return groupChatRepository.getGroupChatsWithLastMessageForUser(userId)
                .stream()
                .map(GroupChatProjectionsMapper::toGroupChatRetrievalDTO)
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
        return groupChatUserRepository.findByGroupChatAndUser(chat, user);
    }

    private List<GroupChatUser> mapToGroupChatUser(List<AccountInfo> users, GroupChat groupChat) {
        return users.stream()
                .map(user -> new GroupChatUser(groupChat, user))
                .collect(Collectors.toList());
    }
}
