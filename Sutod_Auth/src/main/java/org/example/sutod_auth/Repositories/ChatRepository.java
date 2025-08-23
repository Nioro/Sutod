package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.Chat;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE (c.userId = :user1 AND c.user2Id = :user2) OR (c.userId = :user2 AND c.user2Id = :user1)")
    Optional<Chat> findByParticipants(@Param("user1") Long user1, @Param("user2") Long user2);

    List<Chat> findAllByUserId(Long userId);

    List<Chat> findAllByUser2Id(Long user2Id);
}
