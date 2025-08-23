package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceIntr{

    List<User> findAll();

    Optional<User> findByName(String name);

    Optional<User> findById(Long id);

    void addUserToGroup(Long userId, Long groupId);

    void removeUserFromGroup(Long userId, Long groupId);

    void addUserToContacts(Long userId, Long userInContactId);
    void ChangeAvatar(Long userId, String avatar);

    void removeUserFromContacts(Long userId, Long groupId);
    Optional<List<User>> findAllContactsById(Long userId);
    Optional<User> findContactById(Long userId, Long contactId);
}
