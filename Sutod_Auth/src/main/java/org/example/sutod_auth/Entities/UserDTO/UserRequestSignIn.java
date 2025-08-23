package org.example.sutod_auth.Entities.UserDTO;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserRequestSignIn {
    String username;
    String password;
}
