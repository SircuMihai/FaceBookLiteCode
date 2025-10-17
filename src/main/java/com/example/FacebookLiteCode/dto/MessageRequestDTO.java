package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequestDTO {
    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    @JsonProperty("message")
    private String message;
    
    // Optional string date; consider switching to a proper date type later
    @JsonProperty("data")
    private String data;

    @NotNull(message = "Sender user ID is required")
    @JsonProperty("senderUserId")
    private Integer senderUserId;

    @NotNull(message = "Recipient user ID is required")
    @JsonProperty("recipientUserId")
    private Integer recipientUserId;
    
    @JsonProperty("isPin")
    private boolean isPin;
}
