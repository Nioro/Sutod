package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.Message;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

    Message findById(Long id);

    List<Message> findAllByChatId(Long chatId);

    List<Message> findTop50ByChatIdOrderByTimestampDesc(Long chatId);

    List<Message> findNext50ByChatIdBefore(Long chatId, LocalDateTime before, Pageable pageable);

    Message sendMessage(Message message, Long Id);

    boolean hasAccessToChat(Long userId, Long chatId);

    Message updateMessage(Long messageId, String newText, Long userId);

    boolean deleteMessageById(Long messageId, Long userId);

    void deleteAllMessages(Long chatId);

}
