package org.example.sutod_auth.Services.Impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.sutod_auth.Entities.Chat;
import org.example.sutod_auth.Entities.DTO.ChatDTO;
import org.example.sutod_auth.Entities.DTO.GroupDTO;
import org.example.sutod_auth.Entities.DTO.GroupMessageDTO;
import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.GroupMessage;
import org.example.sutod_auth.Entities.Message;
import org.example.sutod_auth.Repositories.MessageGroupRepository;
import org.example.sutod_auth.Services.GroupService;
import org.example.sutod_auth.Services.MessageGroupService;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageGroupServiceImpl implements MessageGroupService {

    MessageGroupRepository messageGroupRepository;

    UserServiceImpl userService;

    @Override
    public List<GroupMessage> findAllByGroupId(Long chatId) {
        return messageGroupRepository.findAllByGroupId(chatId);
    }

    @Override
    public GroupMessage sendMessage(GroupMessageDTO message, Long Id) {
        GroupMessage message1 = new GroupMessage();
        message1.setTimestamp(LocalDateTime.now());
        message1.setGroupId(Id);
        message1.setMessage(message.getMessage());
        message1.setSenderId(message.getSenderId());
        message1.setId(null);
        return messageGroupRepository.save(message1);
    }

    @Override
    public boolean hasAccessToChat(Long userId, Long groupId) {
        return false;
    }

    @Override
    public GroupMessage updateMessage(Long messageId, String newText, Long userId) {
        Optional<GroupMessage> messageOpt = messageGroupRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message not found");
        }

        GroupMessage message = messageOpt.get();
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("You can only edit your own messages");
        }

        message.setMessage(newText);
        return  messageGroupRepository.save(message);
    }

    @Override
    public void deleteMessageById(Long messageId) {
        messageGroupRepository.deleteById(messageId);
    }

    public GroupMessageDTO convertToDto(GroupMessage message, Long currentUserId) {

        String username = userService.findById(message.getSenderId()).get().getUsername();

        return new GroupMessageDTO(message.getId(), message.getMessage(), message.getSenderId(), message.getGroupId(), message.getTimestamp(), username);
    }
}
