package org.example.sutod_auth.Services.Impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.GroupRepository;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor

public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @Override
    public Group createGroupAndAssignToUser(Long userId, Group group){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User creator = userRepository.findById(group.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        group.setCreatorId(group.getCreatorId());

        user.getGroupList().add(group);
        creator.getGroupList().add(group);
        createGroup(group);

        userRepository.save(user);

        userRepository.save(creator);

        return group;
    }

    public Group createGroupAndAssignToUsers(List<Long> userIds, Group group) {
        User creator = userRepository.findById(group.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Long> allUserIds = new ArrayList<>(userIds);
        if (!allUserIds.contains(creator.getId())) {
            allUserIds.add(creator.getId());
        }

        List<User> users = userRepository.findAllByIdWithGroups(allUserIds);

        groupRepository.save(group);

        for (User user : users) {
            user.getGroupList().add(group);
        }

        userRepository.saveAll(users);
        return group;
    }

    @Override
    public Optional<List<Group>> getAllGroupsByUserId(Long userId) {
        return groupRepository.findAllGroupsById(userId);
    }

    @Override
    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findGroupById(id);
    }

    public boolean removeMemberFromGroup(Long id, Long groupId) {
        Optional<Group> group = groupRepository.findById(groupId);

        Optional<List<User>> users = groupRepository.findAllMembersById(groupId);

        for (User user : users.get()) {
            logger.info("Removing member " + user.getId() + " from group " + groupId);
        }

        if(group.isEmpty() || users.isEmpty()) {
            return false;
        }
        else{
            for (User user : users.get()) {
                if (user.getGroupList().contains(group.get()) && Objects.equals(user.getId(), id)) {
                    user.getGroupList().remove(group.get());
                }
            }
        }

        userRepository.saveAll(users.get());
        return true;
    }

    @Override
    public void ChangeAvatar(Long id, Long groupId, String avatar) {
        Optional<Group> group = groupRepository.findById(groupId);
        group.get().setAvatar(avatar);
        groupRepository.save(group.get());
    }

    public Optional<List<User>> getGroupMembers(Long groupId) {
        return groupRepository.findAllMembersById(groupId);
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroup(Long id) {
        return groupRepository.findById(id).orElseThrow();
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
