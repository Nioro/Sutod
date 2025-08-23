package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.Chat;
import org.example.sutod_auth.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u left join fetch u.channelList where u.id = :id")
    Optional<User> findByIdWithChannels(@Param("id") Long id);
    @Query("select distinct u from User u left join fetch u.channelList where u.id in :ids")
    List<User> findAllByIdWithChannels(@Param("ids") List<Long> ids);
    @Query("select distinct u from User u left join fetch u.groupList where u.id in :ids")
    List<User> findAllByIdWithGroups(@Param("ids") List<Long> ids);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    // @Query("select distinct u from User u left join fetch u.contactList where u.id in :id")
    @Query("""
        SELECT u2 
        FROM User u1 
        JOIN u1.contactList u2 
        WHERE u1.id = :id
    """)
    Optional<List<User>> findAllContactsById(Long id);
    // @Query("select distinct u from User u join fetch u.contactList where u.id = 3")
    @Query("""
        SELECT u2 
        FROM User u1 
        JOIN u1.contactList u2 
        WHERE u1.id = :user1 AND u2.id = :user2
    """)
    Optional<User> findContactById(@Param("user1") Long user1, @Param("user2") Long user2);

    Optional<User> findUserById(@Param("id") Long id);
}
