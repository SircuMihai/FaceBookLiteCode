package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;
    
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, message, data, null);
    }
    
    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<>(true, message, null, null);
    }
    
    public static <T> ApiResponseDto<T> error(String error) {
        return new ApiResponseDto<>(false, null, null, error);
    }
    
    public static <T> ApiResponseDto<T> error(String message, String error) {
        return new ApiResponseDto<>(false, message, null, error);
    }
}
