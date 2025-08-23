package org.example.sutod_auth.Entities.UserDTO;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
//user в который вписываюся данные из request
public class UserRequestSignUp {
    String username;
    String email;
    String password;
    String code;
}
