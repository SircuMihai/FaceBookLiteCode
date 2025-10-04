package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDto {
    private String message;
    private String data;
    private boolean isPin;
    private Long userId;
    private Long groupId;
}
