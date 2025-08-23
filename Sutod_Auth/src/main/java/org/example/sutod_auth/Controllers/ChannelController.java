package org.example.sutod_auth.Controllers;

import lombok.AllArgsConstructor;
import org.example.sutod_auth.Entities.Channel;
import org.example.sutod_auth.Entities.DTO.*;
import org.example.sutod_auth.Entities.Group;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Repositories.UserRepository;
import org.example.sutod_auth.Services.Impl.ChannelServiceImpl;
import org.example.sutod_auth.Services.Impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/channel")
@AllArgsConstructor
public class ChannelController {
    private ChannelServiceImpl channelService;

    private UserServiceImpl userService;

    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @PostMapping("/create")
    public ResponseEntity<Channel> createChannel(@RequestBody ChannelDTO requestChannel) {

        if(requestChannel.getChannelName()==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Channel channel = new Channel();
        channel.setChannelName(requestChannel.getChannelName());
        channel.setOwnerId(requestChannel.getOwnerId());

        Channel newChannel = channelService.createChannelAndAssignToUsers(requestChannel.getUsersToAdd(), channel);

        return ResponseEntity.ok(newChannel);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Channel> subscribe(@RequestBody SubscribeDTO subscribeDTO) {
        channelService.subscribe(subscribeDTO.getUserId(), subscribeDTO.getChannelId());
        return ResponseEntity.ok().build() ;
    }

    public ResponseEntity<List<Channel>> getAllChannels() {
        return ResponseEntity.ok(channelService.getAllChannels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ChannelDTO>> getChannels(@PathVariable Long id) {
        Optional<List<Channel>> channels = channelService.getAllChannelsByUserId(id);

        List<ChannelDTO> channelDTO = new ArrayList<>();
        for (var i : channels.get()) {
            channelDTO.add(new ChannelDTO(i.getChannelName(), i.getOwnerId(), i.getId(), i.getAvatar()));
        }

        return ResponseEntity.ok(channelDTO);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ChannelDTO>> getChannelsbyName(@PathVariable String name) {
        Optional<List<Channel>> channels = channelService.getAllChannelsByName(name);

        List<ChannelDTO> channelDTO = new ArrayList<>();
        for (var i : channels.get()) {
            channelDTO.add(new ChannelDTO(i.getChannelName(), i.getOwnerId(), i.getId(), i.getAvatar()));
        }

        return ResponseEntity.ok(channelDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable Long id) {
        channelService.deleteChannel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDTO>> getChannelMembers(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<List<User>> members = channelService.getChannelMembers(id);

        List<UserDTO> usersDTO = new ArrayList<>();
        for (var i : members.get()) {
            usersDTO.add(new UserDTO(i.getId(), i.getUsername(), i.getAvatar()));
        }

        return ResponseEntity.ok(usersDTO);
    }

    @DeleteMapping("/{channelId}/members/{userId}")
    public ResponseEntity<?> removeMemberFromChannel(@PathVariable Long channelId, @PathVariable Long userId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!(channelService.getChannel(channelId).getOwnerId().equals(currentUser.getId())) && !(currentUser.getId().equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only channel admin can remove members");
        }
        
        boolean removed = channelService.removeMemberFromChannel(userId, channelId);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove member");
        }
    }
    @PostMapping("/avatar")
    public ResponseEntity<?> changeAvatar(@RequestBody ChannelAvatarDTO avatarDTO) {
        logger.info(avatarDTO.getAvatar());
        logger.info(avatarDTO.getChannelId().toString());
        logger.info(avatarDTO.getId().toString());
        channelService.ChangeAvatar(avatarDTO.getId(), avatarDTO.getChannelId(), avatarDTO.getAvatar());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/avatar")
    public ResponseEntity<?> getAvatar(@RequestParam(name = "id") Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String avatar = user.getAvatar();
        return ResponseEntity.ok(avatar);
    }
}
