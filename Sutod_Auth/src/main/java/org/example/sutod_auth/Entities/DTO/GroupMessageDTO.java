package org.example.sutod_auth.Entities.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Reference;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageDTO {
    private Long id;

    private String message;

    private Long senderId;

    private Long groupId;

    private LocalDateTime timestamp;

    private String username;

    public GroupMessageDTO(String message, Long senderId) {
        this.message = message;
        this.senderId = senderId;
    }
}
