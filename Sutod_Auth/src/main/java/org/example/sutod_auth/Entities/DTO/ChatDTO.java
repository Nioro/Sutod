package org.example.sutod_auth.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    String username;
    Long userId;
    Long user2Id;
    Long chatId;
    String avatar;
}
