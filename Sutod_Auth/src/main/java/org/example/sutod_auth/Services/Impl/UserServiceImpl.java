package org.example.sutod_auth.Services.Impl;

import lombok.AllArgsConstructor;
import org.example.sutod_auth.Controllers.UserController;
import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.GroupRepository;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.UserServiceIntr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServiceIntr {

    private UserRepository userRepository;
    private GroupRepository groupRepository;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByUsername(name);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    public void addUserToGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();

        user.getGroupList().add(group);
        userRepository.save(user);
    }

    public void removeUserFromGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();

        user.getGroupList().remove(group);
        userRepository.save(user);
    }

    public void addUserToContacts(Long userId, Long userInContactsId) {
        User user = userRepository.findById(userId).orElseThrow();
        User userInContacts = userRepository.findById(userInContactsId).orElseThrow();

        user.getContactList().add(userInContacts);
        userRepository.save(user);
    }

    @Override
    public void ChangeAvatar(Long userId, String avatar) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setAvatar(avatar);
        userRepository.save(user);
    }

    public void removeUserFromContacts(Long userId, Long userInContactsId) {
        User user = userRepository.findById(userId).orElseThrow();
        User userInContacts = userRepository.findById(userInContactsId).orElseThrow();

        user.getContactList().remove(userInContacts);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findContactById(Long userId, Long userInContactsId) {
        return userRepository.findContactById(userId, userInContactsId);
    }

    @Override
    public Optional<List<User>> findAllContactsById(Long userId) {
        return userRepository.findAllContactsById(userId);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }
}
