package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.DTO.GroupMessageDTO;
import org.example.sutod_auth.Entities.GroupMessage;
import org.example.sutod_auth.Entities.Message;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageGroupService {
    List<GroupMessage> findAllByGroupId(Long chatId);

    GroupMessage sendMessage(GroupMessageDTO message, Long Id);

    boolean hasAccessToChat(Long userId, Long groupId);

    GroupMessage updateMessage(Long messageId, String newText, Long userId);

    void deleteMessageById(Long messageId);
}
