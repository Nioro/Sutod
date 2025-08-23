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
@Table(name = "groups", schema = "app_schema")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "c_group_name")
    private String groupName;

    @Reference
    @Column(name = "c_creator_id")
    private Long creatorId;

    @Column(name = "c_avatar")
    String avatar;

    @Column(name = "c_created_at")
    private Date createdAt;

    @ManyToMany(mappedBy = "groupList")
    private Set<User> groupMembers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group other = (Group) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
