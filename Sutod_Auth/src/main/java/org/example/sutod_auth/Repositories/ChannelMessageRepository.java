package org.example.sutod_auth.Repositories;

import org.example.sutod_auth.Entities.ChannelMessage;
import org.example.sutod_auth.Entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {

    //    List<ChannelMessage> findAllByChannelId(Long channelId);
//
//    @Query("SELECT m FROM ChannelMessage m WHERE m.channelId = :channelId ORDER BY m.timestamp DESC")
//    List<ChannelMessage> findTop50ByChannelIdOrderByTimestampDesc(@Param("channelId") Long channelId);
//
//    @Query("SELECT m FROM ChannelMessage m WHERE m.channelId = :channelId AND m.timestamp < :before ORDER BY m.timestamp DESC")
//    List<ChannelMessage> findNext50ByChannelIdBefore(@Param("channelId") Long channelId, @Param("before") LocalDateTime before);
    List<ChannelMessage> findAllByChannelId(Long id);

    List<ChannelMessage> findTop50ByChannelIdOrderByTimestampDesc(Long groupId);

    @Query("SELECT m FROM ChannelMessage m WHERE m.channelId = :chatId AND m.timestamp < :before ORDER BY m.timestamp DESC")
    List<ChannelMessage> findNext50ByChannelIdBefore(@Param("channelId") Long channelId, @Param("before") LocalDateTime before, Pageable pageable);


}




