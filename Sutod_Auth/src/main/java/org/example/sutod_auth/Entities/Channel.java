package org.example.sutod_auth.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Reference;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "channels", schema = "app_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "c_channel_name")
    private String channelName;

    @Reference
    @Column(name = "c_owner_id")
    private Long ownerId;

    @Column(name = "c_avatar")
    String avatar;

    @Column(name = "c_created_at")
    private Date createdAt;

    @ManyToMany(mappedBy = "channelList")
    private Set<User> channelMembers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel)) return false;
        Channel other = (Channel) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
