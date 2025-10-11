package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonAlias;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequestDTO {
    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;
    
    // Optional string date; consider switching to a proper date type later
    @JsonAlias({"data"})
    private String data;

    @NotNull(message = "Sender user ID is required")
    @JsonAlias({"sender_user_id", "senderUserId", "user_id"})
    private Integer senderUserId;

    @NotNull(message = "Recipient user ID is required")
    @JsonAlias({"recipient_user_id", "recipientUserId", "resever_id"})
    private Integer recipientUserId;
    
    @JsonAlias({"is_pin", "pin", "isPin"})
    private boolean isPin;
}
