package org.example.sutod_auth.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    Long senderId;
    Long chatId;
    LocalDateTime dateTime;
    String message;
}
