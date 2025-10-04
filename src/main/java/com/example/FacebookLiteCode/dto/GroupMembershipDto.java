package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMembershipDto {
    private Long userId;
    private Long groupId;
    private boolean isAdmin;
    private String role; // MEMBER, ADMIN, MODERATOR
}
