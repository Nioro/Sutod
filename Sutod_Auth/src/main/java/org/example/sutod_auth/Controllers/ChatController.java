package org.example.sutod_auth.Controllers;

import lombok.AllArgsConstructor;
import org.example.sutod_auth.Entities.Chat;
import org.example.sutod_auth.Entities.DTO.ChatDTO;
import org.example.sutod_auth.Entities.Message;
import org.example.sutod_auth.Repositories.ChatRepository;
import org.example.sutod_auth.Services.Impl.ChatServiceImpl;
import org.example.sutod_auth.Services.Impl.MessageServiceImpl;
import org.example.sutod_auth.Services.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
@AllArgsConstructor
public class ChatController {

    private final ChatServiceImpl chatService;

    private final ChatRepository chatRepository;

    private final MessageServiceImpl messageService;

    @GetMapping("/{id}")
    public ResponseEntity<List<ChatDTO>> getAllChatsByUserId(@PathVariable Long id) {
        List<Chat> chats = chatService.findAllByUserId(id);

        if (chats.isEmpty()){
            List<Chat> chats2 = chatRepository.findAllByUser2Id(id);
            if (!chats2.isEmpty()){
                List<ChatDTO> chatDTO = chats2.stream()
                        .map(chat -> chatService.convertToDto(chat, id))
                        .toList();
                return ResponseEntity.ok(chatDTO);
            }
        };

        List<ChatDTO> chatDTO = chats.stream()
                .map(chat -> chatService.convertToDto(chat, id))
                .toList();
        return ResponseEntity.ok(chatDTO);
    }


    @GetMapping
    public ResponseEntity<List<Chat>> getAllChats() {
        return ResponseEntity.ok(chatRepository.findAll());
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<Optional<Chat>> getChatById(@PathVariable Long id) {
        return ResponseEntity.ok().body(chatService.findById(id));
    }

    @GetMapping("/twoId")
    public ResponseEntity<Long> getAllChatsByUserIdTwoId(@RequestParam Long id1, @RequestParam Long id2) {
        Long Id1 = Math.max(id1, id2);
        Long Id2 = Math.min(id1, id2);
        try {
            Long chatId = chatService.findByParticipants(Id1, Id2).get().getId();
            return ResponseEntity.ok(chatId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        if(chat.getUserId()==null || chat.getUser2Id()==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(chatService.createChat(chat));
    }

    @DeleteMapping("/{userId}/{user2Id}")
    public ResponseEntity<?> deleteChatById(@PathVariable Long userId, @PathVariable Long user2Id) {
        Long Id1 = Math.max(userId, user2Id);
        Long Id2 = Math.min(userId, user2Id);
        Chat chat = chatService.findByParticipants(Id1, Id2).orElseThrow(() -> new RuntimeException("Chat not found"));
        Long id = chat.getId();

        List<Message> messages = messageService.findAllByChatId(id);

        for (Message message : messages) {
            messageService.deleteAllMessages(message.getId());
        }

        chatService.deleteChatById(id);
        return ResponseEntity.ok().build();
    }
}
