package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDto {
    private Long usersGroupsId;
    private Long userId;
    private Long groupId;
    private boolean groupAdmin;
    private String username;
    private String groupName;
    private String joinedAt;
    private String role;
}
