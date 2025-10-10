package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponseDTO {
    private int messageId;
    private String message;
    private String data;
    private boolean isPin;
    private int senderId;
    private String senderUsername;
    private String senderProfilePicture;
}
