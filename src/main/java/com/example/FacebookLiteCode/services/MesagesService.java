package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Groups;
import com.example.FacebookLiteCode.model.Mesages;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.MesagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MesagesService {
    
    @Autowired
    private MesagesRepository mesagesRepository;
    
    public List<Mesages> getAllMessages() {
        return mesagesRepository.findAll();
    }
    
    public Optional<Mesages> getMessageById(int id) {
        return mesagesRepository.findById(id);
    }
    
    public Mesages saveMessage(Mesages message) {
        return mesagesRepository.save(message);
    }
    
    public void deleteMessage(int id) {
        mesagesRepository.deleteById(id);
    }
    
    public List<Mesages> getMessagesByUser(Users user) {
        return mesagesRepository.findByUser(user);
    }
    
    public List<Mesages> getMessagesByGroup(Groups group) {
        return mesagesRepository.findByGroup(group);
    }
    
    public List<Mesages> getMessagesByUserId(int userId) {
        return mesagesRepository.findByUserUserId(userId);
    }
    
    public List<Mesages> getMessagesByGroupId(int groupId) {
        return mesagesRepository.findByGroupGroupId(groupId);
    }
    
    public List<Mesages> getMessagesByIsPin(boolean isPin) {
        return mesagesRepository.findByIsPin(isPin);
    }
    
    public List<Mesages> getMessagesByContent(String message) {
        return mesagesRepository.findByMessageContainingIgnoreCase(message);
    }
}
