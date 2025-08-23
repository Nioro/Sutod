package org.example.sutod_auth.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDTO {
    private String channelName;
    private Long ownerId;
    private List<Long> usersToAdd;
    private Long channelId;
    private String avatar;
    public ChannelDTO(String channelName, Long ownerId, Long channelId, String avatar) {
        this.channelName = channelName;
        this.ownerId = ownerId;
        this.channelId = channelId;
        this.avatar = avatar;
    }
}
