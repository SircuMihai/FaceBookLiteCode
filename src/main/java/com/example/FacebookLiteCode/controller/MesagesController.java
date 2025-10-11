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
    
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createMessage(@Valid @RequestBody MessageRequestDTO request) {
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
        return mesagesService.getConversationDTO(user1Id, user2Id);
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
