package com.example.FacebookLiteCode.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private int status;
    private String error;
    private String message;
    private String details;
    private String path;
    private LocalDateTime timestamp;
    
    public static ErrorResponse builder() {
        ErrorResponse response = new ErrorResponse();
        response.timestamp = LocalDateTime.now();
        return response;
    }
    
    public ErrorResponse status(int status) {
        this.status = status;
        return this;
    }
    
    public ErrorResponse error(String error) {
        this.error = error;
        return this;
    }
    
    public ErrorResponse message(String message) {
        this.message = message;
        return this;
    }
    
    public ErrorResponse details(String details) {
        this.details = details;
        return this;
    }
    
    public ErrorResponse path(String path) {
        this.path = path;
        return this;
    }
    
    public ErrorResponse build() {
        return this;
    }
}
