package org.example.sutod_auth.Services.Impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.sutod_auth.Entities.ChannelMessage;
import org.example.sutod_auth.Entities.DTO.ChannelMessageDTO;
import org.example.sutod_auth.Entities.DTO.GroupMessageDTO;
import org.example.sutod_auth.Entities.GroupMessage;
import org.example.sutod_auth.Repositories.ChannelMessageRepository;
import org.example.sutod_auth.Repositories.ChannelRepository;
import org.example.sutod_auth.Services.ChannelMessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChannelMessageServiceImpl implements ChannelMessageService {

    private final ChannelRepository channelRepository;
    ChannelMessageRepository messageChannelRepository;

    UserServiceImpl userService;

    @Override
    public List<ChannelMessage> findAllByChannelId(Long channelId) {
        return messageChannelRepository.findAllByChannelId(channelId);
    }

    @Override
    public ChannelMessage sendMessage(ChannelMessageDTO message, Long Id) {
        ChannelMessage channelMessage = new ChannelMessage();
        channelMessage.setTimestamp(LocalDateTime.now());
        channelMessage.setChannelId(Id);
        channelMessage.setMessage(message.getMessage());
        channelMessage.setSenderId(message.getSenderId());
        channelMessage.setId(null);
        return messageChannelRepository.save(channelMessage);
    }

    @Override
    public ChannelMessage updateMessage(Long messageId, String newText, Long userId) {
        Optional<ChannelMessage> messageOpt = messageChannelRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message not found");
        }

        ChannelMessage message = messageOpt.get();
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("You can only edit your own messages");
        }

        message.setMessage(newText);
        return  messageChannelRepository.save(message);
    }

    @Override
    public void deleteMessageById(Long messageId) {
        messageChannelRepository.deleteById(messageId);
    }

    public boolean isChannelAdmin(Long userId, Long channelId){
        return channelRepository.findById(channelId).get().getOwnerId().equals(userId);
    }

    public ChannelMessageDTO convertToDto(ChannelMessage message, Long currentUserId) {

        String username = userService.findById(message.getSenderId()).get().getUsername();

        return new ChannelMessageDTO(message.getId(), message.getMessage(), message.getSenderId(), message.getChannelId(), message.getTimestamp(), username);
    }
}
