package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendshipResponseDTO {
    private int friendshipId;
    private int user1Id;
    private int user2Id;
    private String status;
}
