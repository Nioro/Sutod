package org.example.sutod_auth.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "users", schema = "app_schema")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "c_username")
    String username;

    @Column(name = "c_email")
    String email;

    @Column(name = "c_password")
    String password;

    @Column(name = "c_is_online")
    boolean is_online;

    @Column(name = "c_avatar")
    String avatar;

    @ManyToMany
    @JoinTable(
            name = "groups_users",
            schema = "app_schema",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groupList;

    @ManyToMany
    @JoinTable(
            name = "channels_users",
            schema = "app_schema",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private Set<Channel> channelList;

    @ManyToMany
    @JoinTable(
            name = "contacts",
            schema = "app_schema",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_in_contact_id")
    )
    private Set<User> contactList;

    @ManyToMany(mappedBy = "contactList")
    private Set<User> inContact;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}

