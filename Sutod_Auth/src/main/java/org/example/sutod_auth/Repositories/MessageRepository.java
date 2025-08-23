package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

    List<Message> findAllByChatId(Long id);

    List<Message> findTop50ByChatIdOrderByTimestampDesc(Long chatId);

    // Следующие 50 сообщений до определённого времени (для подгрузки)
    @Query("SELECT m FROM Message m WHERE m.chatId = :chatId AND m.timestamp < :before ORDER BY m.timestamp DESC")
    List<Message> findNext50ByChatIdBefore(@Param("chatId") Long chatId, @Param("before") LocalDateTime before, Pageable pageable);

}
