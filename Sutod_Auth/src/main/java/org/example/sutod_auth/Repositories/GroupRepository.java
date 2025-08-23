package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("""
        SELECT u2 
        FROM Group u1 
        JOIN u1.groupMembers u2 
        WHERE u1.id = :id
    """)
    Optional<List<User>> findAllMembersById(Long id);

    @Query("""
        SELECT u2 
        FROM User u1 
        JOIN u1.groupList u2 
        WHERE u1.id = :id
    """)
    Optional<List<Group>> findAllGroupsById(Long id);

    Optional<Group> findGroupById(Long id);
}
