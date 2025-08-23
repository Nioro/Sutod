package org.example.sutod_auth.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.sutod_auth.Entities.DTO.GroupMessageDTO;
import org.example.sutod_auth.Entities.DTO.MessageDTO;
import org.example.sutod_auth.Entities.GroupMessage;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.Impl.MessageGroupServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group-messages")
@RequiredArgsConstructor
public class MessageGroupController {

    private final MessageGroupServiceImpl messageGroupService;
    private final UserRepository userRepository;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupMessageDTO>> getGroupMessages(@PathVariable Long groupId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        

        
        List<GroupMessage> messages = messageGroupService.findAllByGroupId(groupId);
        if(!messages.isEmpty()){
            List<GroupMessageDTO> messagesDTO = messages.stream()
                    .map(msg -> messageGroupService.convertToDto(msg, currentUser.getId()))
                    .toList();
            return ResponseEntity.ok(messagesDTO);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/{groupId}")
    public ResponseEntity<GroupMessage> sendGroupMessage(@RequestBody GroupMessageDTO message, @PathVariable Long groupId) {
        GroupMessage savedMessage = messageGroupService.sendMessage(message, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupMessage> updateGroupMessage(@PathVariable Long id, @RequestBody GroupMessage messageUpdate, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMessage updatedMessage = messageGroupService.updateMessage(id, messageUpdate.getMessage(), currentUser.getId());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupMessage(@PathVariable Long id) {
        messageGroupService.deleteMessageById(id);
        return ResponseEntity.ok().build();
    }
}






