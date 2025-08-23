package org.example.sutod_auth.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeDTO {
    private Long userId;
    private Long channelId;
}
