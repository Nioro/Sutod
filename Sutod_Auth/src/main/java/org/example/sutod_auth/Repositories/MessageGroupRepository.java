package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageGroupRepository extends JpaRepository<GroupMessage, Long> {

    List<GroupMessage> findAllByGroupId(Long id);

    List<GroupMessage> findTop50ByGroupIdOrderByTimestampDesc(Long groupId);

    @Query("SELECT m FROM GroupMessage m WHERE m.groupId = :chatId AND m.timestamp < :before ORDER BY m.timestamp DESC")
    List<GroupMessage> findNext50ByGroupIdBefore(@Param("groupId") Long groupId, @Param("before") LocalDateTime before, Pageable pageable);

}
