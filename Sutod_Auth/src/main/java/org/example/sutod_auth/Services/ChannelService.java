package org.example.sutod_auth.Services;

import org.example.sutod_auth.Entities.Channel;
import org.example.sutod_auth.Entities.User;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    Channel createChannelAndAssignToUser(Long userId, Channel channel);
    Channel createChannelAndAssignToUsers(List<Long> userIds, Channel channel);

    void ChangeAvatar(Long id, Long channelId, String avatar);

    Optional<List<User>> getChannelMembers(Long channelId);
}
