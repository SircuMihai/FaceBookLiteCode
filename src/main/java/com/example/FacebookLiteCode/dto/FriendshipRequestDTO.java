package com.example.FacebookLiteCode.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonAlias;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendshipRequestDTO {
    @NotNull(message = "user1Id is required")
    @JsonAlias({"user1id", "user1Id"})
    private Integer user1Id;

    @NotNull(message = "user2Id is required")
    @JsonAlias({"user2id", "user2Id"})
    private Integer user2Id;

    private String status;
}
