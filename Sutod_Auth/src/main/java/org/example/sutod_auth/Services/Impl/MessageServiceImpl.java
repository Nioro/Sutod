package org.example.sutod_auth.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.example.sutod_auth.Entities.Chat;
import org.example.sutod_auth.Entities.Message;
import org.example.sutod_auth.Repositories.MessageRepository;
import org.example.sutod_auth.Services.MessageService;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final ChatServiceImpl chatService;

    @Override
    public Message findById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
    }

    @Override
    public List<Message> findAllByChatId(Long chatId) {
        return messageRepository.findAllByChatId(chatId);
    }

    @Override
    public List<Message> findTop50ByChatIdOrderByTimestampDesc(Long chatId) {
        return messageRepository.findTop50ByChatIdOrderByTimestampDesc(chatId);
    }

    @Override
    public List<Message> findNext50ByChatIdBefore(Long chatId, LocalDateTime before, Pageable pageable) {
        return messageRepository.findNext50ByChatIdBefore(chatId,before,pageable);
    }

    @Override
    public Message sendMessage(Message message, Long Id) {

        Long id1 = Math.max(message.getSenderId(), Id);
        Long id2 = Math.min(message.getSenderId(), Id);

        Chat chat;
        chat = chatService.findByParticipants(id1, id2).orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.setUserId(id1);
            newChat.setUser2Id(id2);
            return chatService.createChat(newChat);
        });

        message.setChatId(chat.getId());
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    @Override
    public boolean hasAccessToChat(Long userId, Long chatId) {
        Optional<Chat> chatOpt = chatService.findById(chatId);
        if (chatOpt.isEmpty()) {
            return false;
        }
        Chat chat = chatOpt.get();
        return userId.equals(chat.getUserId()) || userId.equals(chat.getUser2Id());
    }

    @Override
    public Message updateMessage(Long messageId, String newText, Long userId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message not found");
        }
        
        Message message = messageOpt.get();
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("You can only edit your own messages");
        }
        
        message.setMessage(newText);
        return messageRepository.save(message);
    }

    @Override
    public boolean deleteMessageById(Long messageId, Long userId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return false;
        }
        
        Message message = messageOpt.get();
        if (!message.getSenderId().equals(userId)) {
            return false; // Пользователь не может удалить чужое сообщение
        }
        
        messageRepository.deleteById(messageId);
        return true;
    }

    @Override
    public void deleteAllMessages(Long chatId) {
        messageRepository.deleteById(chatId);
    }
}
