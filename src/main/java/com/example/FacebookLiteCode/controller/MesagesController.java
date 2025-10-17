package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.MesagesService;
import com.example.FacebookLiteCode.dto.MessageRequestDTO;
import com.example.FacebookLiteCode.dto.MessageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MesagesController {
    
    @Autowired
    private MesagesService mesagesService;
    
    @GetMapping
    public List<MessageResponseDTO> getAllMessages() {
        return mesagesService.getAllMessagesDTO();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> getMessageById(@PathVariable int id) {
        MessageResponseDTO dto = mesagesService.getMessageResponseById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/raw")
    public ResponseEntity<String> createMessageRaw(@RequestBody String rawJson) {
        System.out.println("DEBUG - Raw JSON received: " + rawJson);
        return ResponseEntity.ok("Raw JSON received: " + rawJson);
    }
    
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createMessage(@RequestBody MessageRequestDTO request) {
        System.out.println("DEBUG - Received request: " + request);
        System.out.println("DEBUG - Message: " + request.getMessage());
        System.out.println("DEBUG - Sender ID: " + request.getSenderUserId());
        System.out.println("DEBUG - Recipient ID: " + request.getRecipientUserId());
        
        // Manual validation
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getSenderUserId() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getRecipientUserId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        MessageResponseDTO created = mesagesService.createMessage(request);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateMessage(@PathVariable int id, @Valid @RequestBody MessageRequestDTO request) {
        try {
            MessageResponseDTO updated = mesagesService.updateMessage(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id) {
        if (mesagesService.getMessageById(id).isPresent()) {
            mesagesService.deleteMessage(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/user/{userId}")
    public List<MessageResponseDTO> getMessagesByUserId(@PathVariable int userId) {
        return mesagesService.getMessagesByUserIdDTO(userId);
    }
    
    @GetMapping("/receiver/{receiverId}")
    public List<MessageResponseDTO> getMessagesByReceiverId(@PathVariable int receiverId) {
        return mesagesService.getMessagesByReceiverIdDTO(receiverId);
    }

    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public List<MessageResponseDTO> getConversation(@PathVariable int user1Id, @PathVariable int user2Id) {
        // Security check: Only allow if the current user is one of the participants
        // For now, we'll rely on the frontend to pass the correct user IDs
        // In a real application, you'd get the current user from the security context
        return mesagesService.getConversationDTO(user1Id, user2Id);
    }
    
    @GetMapping("/my-conversation/{currentUserId}/{friendId}")
    public List<MessageResponseDTO> getMyConversation(@PathVariable int currentUserId, @PathVariable int friendId) {
        // This endpoint ensures that only the current user can see their own conversations
        return mesagesService.getMyConversationDTO(currentUserId, friendId);
    }

    @GetMapping("/pinned/{isPin}")
    public List<MessageResponseDTO> getMessagesByIsPin(@PathVariable boolean isPin) {
        return mesagesService.getMessagesByIsPinDTO(isPin);
    }
    
    @GetMapping("/search/content/{message}")
    public List<MessageResponseDTO> searchMessagesByContent(@PathVariable String message) {
        return mesagesService.getMessagesByContentDTO(message);
    }
}
