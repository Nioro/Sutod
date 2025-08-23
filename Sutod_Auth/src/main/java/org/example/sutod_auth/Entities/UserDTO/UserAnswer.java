package org.example.sutod_auth.Entities.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAnswer {
    private String username;
    private String avatar;
    private Long id;
}
