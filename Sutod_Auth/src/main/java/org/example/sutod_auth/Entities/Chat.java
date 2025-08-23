package org.example.sutod_auth.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Reference;

@Entity
@Table(name = "chats", schema = "app_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Reference
    @Column(name = "c_user_id")
    private Long userId;

    @Reference
    @Column(name = "c_user_id_2")
    private Long user2Id;
}
