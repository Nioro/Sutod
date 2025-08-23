package org.example.sutod_auth.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMessageDTO {
    private Long id;

    private String message;

    private Long senderId;

    private Long channelId;

    private LocalDateTime timestamp;

    private String username;

    public ChannelMessageDTO(String message, Long senderId) {
        this.message = message;
        this.senderId = senderId;
    }
}
