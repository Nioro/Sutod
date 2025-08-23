package org.example.sutod_auth.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Reference;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", schema = "app_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "c_message_text")
    private String message;

    @Reference
    @Column(name = "c_sender_id")
    private Long senderId;

    @Reference
    @Column(name = "c_chat_id")
    private Long chatId;

    @Column(name = "c_timestamp")
    private LocalDateTime timestamp;

}
