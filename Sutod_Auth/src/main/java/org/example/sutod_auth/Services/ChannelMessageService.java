package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.ChannelMessage;
import org.example.sutod_auth.Entities.DTO.ChannelMessageDTO;
import org.example.sutod_auth.Entities.DTO.GroupMessageDTO;
import org.example.sutod_auth.Entities.GroupMessage;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface ChannelMessageService {
    List<ChannelMessage> findAllByChannelId(Long chatId);

    ChannelMessage sendMessage(ChannelMessageDTO message, Long Id);

    ChannelMessage updateMessage(Long messageId, String newText, Long userId);

    void deleteMessageById(Long messageId);
}




