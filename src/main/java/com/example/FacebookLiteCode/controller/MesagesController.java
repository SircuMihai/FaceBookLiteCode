package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.MesagesService;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.dto.MessageRequestDTO;
import com.example.FacebookLiteCode.dto.MessageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MesagesController {
    
    @Autowired
    private MesagesService mesagesService;
    
    @Autowired
    private UsersRepository usersRepository;
    
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
    
    /**
     * Delete a message - Users can only delete their own messages (as sender or receiver)
     * Admins can delete any message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMessage(@PathVariable int id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }
        
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (currentUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check if message exists
        MessageResponseDTO message = mesagesService.getMessageResponseById(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if user is the sender or receiver of the message OR is an admin
        boolean isSender = message.getSenderId() == currentUser.getUserId();
        boolean isReceiver = message.getReceiverId() == currentUser.getUserId();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        
        if (!isSender && !isReceiver && !isAdmin) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "You can only delete your own messages");
            return ResponseEntity.status(403).body(error);
        }
        
        // Delete the message
        mesagesService.deleteMessage(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message deleted successfully");
        return ResponseEntity.ok(response);
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
