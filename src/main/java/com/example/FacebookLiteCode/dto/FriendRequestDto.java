package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private Long userId;
    private Long friendId;
    private String status; // PENDING, ACCEPTED, DECLINED, BLOCKED
}
