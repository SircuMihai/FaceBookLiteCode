package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Mesages;
import com.example.FacebookLiteCode.services.MesagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MesagesController {
    
    @Autowired
    private MesagesService mesagesService;
    
    @GetMapping
    public List<Mesages> getAllMessages() {
        return mesagesService.getAllMessages();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Mesages> getMessageById(@PathVariable int id) {
        Optional<Mesages> message = mesagesService.getMessageById(id);
        return message.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Mesages createMessage(@RequestBody Mesages message) {
        return mesagesService.saveMessage(message);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Mesages> updateMessage(@PathVariable int id, @RequestBody Mesages message) {
        if (mesagesService.getMessageById(id).isPresent()) {
            message.setMessageId(id);
            return ResponseEntity.ok(mesagesService.saveMessage(message));
        }
        return ResponseEntity.notFound().build();
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
    public List<Mesages> getMessagesByUserId(@PathVariable int userId) {
        return mesagesService.getMessagesByUserId(userId);
    }
    
    @GetMapping("/receiver/{receiverId}")
    public List<Mesages> getMessagesByReceiverId(@PathVariable int receiverId) {
        return mesagesService.getMessagesByReceiverId(receiverId);
    }

    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public List<Mesages> getConversation(@PathVariable int user1Id, @PathVariable int user2Id) {
        return mesagesService.getConversation(user1Id, user2Id);
    }

    @GetMapping("/pinned/{isPin}")
    public List<Mesages> getMessagesByIsPin(@PathVariable boolean isPin) {
        return mesagesService.getMessagesByIsPin(isPin);
    }
    
    @GetMapping("/search/content/{message}")
    public List<Mesages> searchMessagesByContent(@PathVariable String message) {
        return mesagesService.getMessagesByContent(message);
    }
}
