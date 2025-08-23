package org.example.sutod_auth.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String groupName;
    private Long creatorId;
    private Long groupId;
    private List<Long> usersToAdd;
    private String avatar;

    public GroupDTO(String groupName, Long creatorId, Long groupId, String avatar) {
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.avatar = avatar;
    }
}
