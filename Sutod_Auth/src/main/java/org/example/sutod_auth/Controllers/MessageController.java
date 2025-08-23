package org.example.sutod_auth.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.sutod_auth.Entities.Chat;
import org.example.sutod_auth.Entities.Message;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.Impl.ChatServiceImpl;
import org.example.sutod_auth.Services.Impl.MessageServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageServiceImpl messageService;

    private final UserRepository userRepository;

    private final ChatServiceImpl chatService;

    @GetMapping("/chat/{chatId}/recent")
    public ResponseEntity<List<Message>> getRecentMessages(@PathVariable Long chatId, Principal principal) {

        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        if (!messageService.hasAccessToChat(currentUser.getId(), chatId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<Message> messages = messageService.findTop50ByChatIdOrderByTimestampDesc(chatId);

        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/twoId")
    public ResponseEntity<List<Message>> getAllMessages(@RequestParam("id1") Long id1, @RequestParam("id2") Long id2) {

        Chat chat1 = chatService.findByParticipants(id1, id2).orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat2 = chatService.findByParticipants(id2, id1).orElseThrow(() -> new RuntimeException("User not found"));

        if(chat1 == null || chat2 == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(chat1 != null){
            return ResponseEntity.ok(messageService.findAllByChatId(chat1.getId()));
        }

        else{
            return ResponseEntity.ok(messageService.findAllByChatId(chat2.getId()));
        }

    }

    @PostMapping("/send/{id}")
    public ResponseEntity<Message> createMessage(@RequestBody Message message, @PathVariable Long id) {
        Message messageSaved = messageService.sendMessage(message, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @RequestBody Message messageUpdate, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        Message updatedMessage = messageService.updateMessage(id, messageUpdate.getMessage(), currentUser.getId());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessageById(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));



        Message message = messageService.findById(id);

        boolean deleted = messageService.deleteMessageById(id, currentUser.getId());
        if (deleted) {
            List<Message> messages = messageService.findAllByChatId(currentUser.getId());
            if(messages.isEmpty()){
                chatService.deleteChatById(message.getId());

            }
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own messages");
        }
    }
}
