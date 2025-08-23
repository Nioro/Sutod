package org.example.sutod_auth.Controllers;

import lombok.AllArgsConstructor;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.sutod_auth.Entities.DTO.AvatarDTO;
import org.example.sutod_auth.Entities.DTO.ContactDTO;
import org.example.sutod_auth.Entities.DTO.UserDTO;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Entities.UserDTO.UserAnswer;
import org.example.sutod_auth.Jwt.JwtCore;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.Impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserServiceImpl userService;

    private JwtCore jwtCore;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.ok().body(authentication.getName()); // вернёт username
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        User user = userService.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UserAnswer userAnswer = new UserAnswer(user.getUsername(), user.getAvatar(), user.getId());
        return ResponseEntity.ok().body(userAnswer);
    }

    @GetMapping("/id/{jwt}")
    public ResponseEntity<?> getUserByJwt(@PathVariable String jwt) {
        User user = userService.findByName(jwtCore.getNameJwt(jwt)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok().body(user.getId());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserAnswer userAnswer = new UserAnswer(user.getUsername(), user.getAvatar(), user.getId());
        return ResponseEntity.ok().body(userAnswer);
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        if(user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is empty");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PatchMapping("/{id}/username")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> userName) {

        String username = userName.get("username");

        if(username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is empty");
        }

        if (userService.findByName(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already in use");
        }

        User user = userService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already in use");
        }

        user.setUsername(username);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id, @RequestBody Map<String, String> passwords) {

        String password1 = passwords.get("password1");

        String password2 = passwords.get("password2");

       if(password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty() || password1.equals(password2)) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is empty");
       }

        User user = userService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(user.getPassword().equals(passwordEncoder.encode(password1)) && !user.getPassword().equals(passwordEncoder.encode(password2))) {
            user.setPassword(passwordEncoder.encode(password2));
            return ResponseEntity.ok(userRepository.save(user));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords don't match");

    }

    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<?> addUserToGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<?> removeUserFromGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.removeUserFromGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contacts/add")
    public ResponseEntity<?> addUserToContacts(@RequestBody ContactDTO contactDTO) {
        if (contactDTO.getContactId().equals(contactDTO.getUserId())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You're trying to add yourself to contact");
        }
        userService.addUserToContacts(contactDTO.getUserId(), contactDTO.getContactId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/contacts/delete")
    public ResponseEntity<?> removeUserFromContacts(@RequestBody ContactDTO contactDTO) {
        userService.removeUserFromContacts(contactDTO.getUserId(), contactDTO.getContactId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/contacts/findAll/{id}")
    public ResponseEntity<List<UserDTO>> getAllContacts(@PathVariable Long id) {
        Optional<List<User>> users = userService.findAllContactsById(id);
        List<UserDTO> usersDTO = new ArrayList<>();
        for (var i : users.get()) {
            usersDTO.add(new UserDTO(i.getId(), i.getUsername(), i.getAvatar()));
        }
        return ResponseEntity.ok(usersDTO);
    }

    @GetMapping("/contacts/find")
    public ResponseEntity<User> getContact(@RequestParam(name = "id1") Long userId, @RequestParam(name = "id2") Long contactId) {
        logger.info("User id " + userId + " and contact id " + contactId);
        User user = userService.findContactById(userId, contactId).orElseThrow(() -> new RuntimeException("Contact not found"));;
        return ResponseEntity.ok(user);
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> changeAvatar(@RequestBody AvatarDTO avatarDTO) {
        userService.ChangeAvatar(avatarDTO.getId(), avatarDTO.getAvatar());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avatar")
    public ResponseEntity<?> getAvatar(@RequestParam(name = "id") Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String avatar = user.getAvatar();
        return ResponseEntity.ok(avatar);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody UserDTO userDTO) {
        userService.deleteUser(userDTO.getId());
        return ResponseEntity.ok().build();
    }
}
