package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.Channel;
import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("""
        SELECT u2 
        FROM Channel u1 
        JOIN u1.channelMembers u2 
        WHERE u1.id = :id
    """)
    Optional<List<User>> findAllMembersById(Long id);
    @Query("""
        SELECT u2 
        FROM User u1 
        JOIN u1.channelList u2 
        WHERE u1.id = :id
    """)
    Optional<List<Channel>> findAllChannelsById(Long id);
    Optional<List<Channel>> findChannelsByChannelName(String channelName);
    void deleteMemberUsersById(Long id);
}
