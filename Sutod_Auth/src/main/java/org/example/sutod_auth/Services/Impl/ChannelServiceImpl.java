package org.example.sutod_auth.Services.Impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.sutod_auth.Entities.Channel;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.ChannelRepository;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @Override
    public Channel createChannelAndAssignToUser(Long userId, Channel channel){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User owner = userRepository.findById(channel.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        channel.setOwnerId(owner.getId());

        user.getChannelList().add(channel);
        owner.getChannelList().add(channel);
        createChannel(channel);

        userRepository.save(user);

        userRepository.save(owner);

        return channel;
    }

    public Channel subscribe(Long userId, Long channelId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));

        user.getChannelList().add(channel);

        userRepository.save(user);

        return channel;
    }

    public Channel createChannelAndAssignToUsers(List<Long> userIds, Channel channel) {
        User owner = userRepository.findById(channel.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Long> allUserIds = new ArrayList<>(userIds);
        if (!allUserIds.contains(owner.getId())) {
            allUserIds.add(owner.getId());
        }

        List<User> users = userRepository.findAllByIdWithChannels(allUserIds);

        channelRepository.save(channel);

        for (User user : users) {
            user.getChannelList().add(channel);
        }

        userRepository.saveAll(users);
        return channel;
    }

    public boolean removeMemberFromChannel(Long id, Long channelId) {
        Optional<Channel> channel = channelRepository.findById(channelId);

        Optional<List<User>> users = channelRepository.findAllMembersById(channelId);

        if(!(channel.isPresent() || users.isPresent())) {
            return false;
        }
        else{
            for (User user : users.get()) {
                if (user.getChannelList().contains(channel.get()) && Objects.equals(user.getId(), id)) {
                    user.getChannelList().remove(channel.get());
                }
            }
        }

        userRepository.saveAll(users.get());
        return true;
    }

    @Override
    public void ChangeAvatar(Long id, Long channelId, String avatar) {
        Optional<Channel> channel = channelRepository.findById(channelId);
        if(channel.get().getOwnerId() == id){
            channel.get().setAvatar(avatar);
            channelRepository.save(channel.get());
        }
    }

    @Override
    public Optional<List<User>> getChannelMembers(Long channelId) {
        return channelRepository.findAllMembersById(channelId);
    }

    public Channel createChannel(Channel channel) {
        return channelRepository.save(channel);
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    public Optional<List<Channel>> getAllChannelsByUserId(Long userId) {
        return channelRepository.findAllChannelsById(userId);
    }

    public Optional<List<Channel>> getAllChannelsByName(String name) {
        return channelRepository.findChannelsByChannelName(name);
    }

    public Channel getChannel(Long id) {
        return channelRepository.findById(id).orElseThrow();
    }

    public void deleteChannel(Long id) {
        channelRepository.deleteById(id);
    }
}
