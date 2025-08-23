package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.Chat;
import org.example.sutod_auth.Entities.DTO.ChatDTO;

import java.util.List;
import java.util.Optional;

public interface ChatService {

    List<Chat> findAllByUserId(Long id);

    Optional<Chat> findById(Long id);

    Optional<Chat> findByParticipants(Long userId, Long user2Id);

    Chat createChat(Chat chat);

    void deleteChatById(Long id);

    ChatDTO convertToDto(Chat chat, Long currentUserId);
}
