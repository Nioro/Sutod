package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    Group createGroupAndAssignToUser(Long userId, Group group);
    Group createGroupAndAssignToUsers(List<Long> userIds, Group group);
    Optional<List<Group>> getAllGroupsByUserId(Long userId);
    Optional<Group> getGroupById(Long id);
    void ChangeAvatar(Long id, Long groupId, String avatar);
}
