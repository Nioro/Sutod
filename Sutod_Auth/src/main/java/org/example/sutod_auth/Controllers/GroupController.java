package org.example.sutod_auth.Controllers;

import lombok.AllArgsConstructor;
import org.example.sutod_auth.Entities.DTO.AvatarDTO;
import org.example.sutod_auth.Entities.DTO.GroupAvatarDTO;
import org.example.sutod_auth.Entities.DTO.GroupDTO;
import org.example.sutod_auth.Entities.DTO.UserDTO;
import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.Impl.GroupServiceImpl;
import org.example.sutod_auth.Services.Impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {
    private GroupServiceImpl groupService;
    private final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestBody GroupDTO requestGroup) {

        if(requestGroup.getGroupName()==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Group group = new Group();
//        group.setAvatar(requestGroup.getAvatar());
        group.setGroupName(requestGroup.getGroupName());
        group.setCreatorId(requestGroup.getCreatorId());

        Group newGroup = groupService.createGroupAndAssignToUsers(requestGroup.getUsersToAdd(), group);

        return ResponseEntity.ok(newGroup);
    }
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<GroupDTO>> getGroup(@PathVariable Long id) {
        Optional<List<Group>> groups = groupService.getAllGroupsByUserId(id);

        List<GroupDTO> groupsDTO = new ArrayList<>();
        for (var i : groups.get()) {
            groupsDTO.add(new GroupDTO(i.getGroupName(), i.getCreatorId(), i.getId(), i.getAvatar()));
        }

        return ResponseEntity.ok(groupsDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDTO>> getGroupMembers(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<List<User>> members = groupService.getGroupMembers(id);

        List<UserDTO> usersDTO = new ArrayList<>();
        for (var i : members.get()) {
            usersDTO.add(new UserDTO(i.getId(), i.getUsername(), i.getAvatar()));
        }

        return ResponseEntity.ok(usersDTO);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!(groupService.getGroup(groupId).getCreatorId().equals(currentUser.getId())) && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only group admin can remove members");
        }
        
        boolean removed = groupService.removeMemberFromGroup(userId, groupId);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove member");
        }
    }
    @PostMapping("/avatar")
    public ResponseEntity<?> changeAvatar(@RequestBody GroupAvatarDTO avatarDTO) {
        groupService.ChangeAvatar(avatarDTO.getId(), avatarDTO.getGroupId(), avatarDTO.getAvatar());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avatar")
    public ResponseEntity<?> getAvatar(@RequestParam(name = "id") Long groupId) {
        Group group = groupService.getGroup(groupId);
        String avatar = group.getAvatar();
        return ResponseEntity.ok(avatar);
    }
}
