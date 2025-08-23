package org.example.sutod_auth.Controllers;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.sutod_auth.Entities.ChannelMessage;
import org.example.sutod_auth.Entities.DTO.ChannelMessageDTO;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.Impl.ChannelMessageServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/channel-messages")
@AllArgsConstructor
public class ChannelMessageController {

    private final ChannelMessageServiceImpl channelMessageService;
    private final UserRepository userRepository;

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<ChannelMessage>> getChannelMessages(@PathVariable Long channelId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        List<ChannelMessage> messages = channelMessageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send/{channelId}")
    public ResponseEntity<ChannelMessage> sendChannelMessage(@RequestBody ChannelMessageDTO message, @PathVariable Long channelId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        // Только администратор канала может отправлять сообщения
        if (!channelMessageService.isChannelAdmin(currentUser.getId(), channelId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        message.setSenderId(currentUser.getId());
        ChannelMessage savedMessage = channelMessageService.sendMessage(message, channelId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChannelMessage> updateChannelMessage(@PathVariable Long id, @RequestBody ChannelMessage messageUpdate, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        ChannelMessage updatedMessage = channelMessageService.updateMessage(id, messageUpdate.getMessage(), currentUser.getId());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannelMessage(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        channelMessageService.deleteMessageById(id);
        return ResponseEntity.ok().build();
    }
}




