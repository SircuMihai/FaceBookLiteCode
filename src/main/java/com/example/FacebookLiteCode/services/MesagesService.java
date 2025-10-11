package com.example.FacebookLiteCode.services;


import com.example.FacebookLiteCode.model.Mesages;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.MesagesRepository;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.dto.MessageRequestDTO;
import com.example.FacebookLiteCode.dto.MessageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MesagesService {
    
    @Autowired
    private MesagesRepository mesagesRepository;

    @Autowired
    private UsersRepository usersRepository;
    
    public List<Mesages> getAllMessages() {
        return mesagesRepository.findAll();
    }
    
    // DTO list methods
    public List<MessageResponseDTO> getAllMessagesDTO() {
        return toResponseDTOList(mesagesRepository.findAll());
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
    
    public List<Mesages> getMessagesByUserId(int userId) {
        return mesagesRepository.findByUserUserId(userId);
    }
    public List<MessageResponseDTO> getMessagesByUserIdDTO(int userId) {
        return toResponseDTOList(mesagesRepository.findByUserUserId(userId));
    }

    public List<Mesages> getMessagesByReceiver(Users receiver) {
        return mesagesRepository.findByResever(receiver);
    }

    public List<Mesages> getMessagesByReceiverId(int receiverId) {
        return mesagesRepository.findByReseverUserId(receiverId);
    }
    public List<MessageResponseDTO> getMessagesByReceiverIdDTO(int receiverId) {
        return toResponseDTOList(mesagesRepository.findByReseverUserId(receiverId));
    }

    public List<Mesages> getMessagesByIsPin(boolean isPin) {
        return mesagesRepository.findByIsPin(isPin);
    }
    public List<MessageResponseDTO> getMessagesByIsPinDTO(boolean isPin) {
        return toResponseDTOList(mesagesRepository.findByIsPin(isPin));
    }
    
    public List<Mesages> getMessagesByContent(String message) {
        return mesagesRepository.findByMessageContainingIgnoreCase(message);
    }
    public List<MessageResponseDTO> getMessagesByContentDTO(String message) {
        return toResponseDTOList(mesagesRepository.findByMessageContainingIgnoreCase(message));
    }

    public List<Mesages> getConversation(int user1Id, int user2Id) {
        return mesagesRepository.findConversation(user1Id, user2Id);
    }
    public List<MessageResponseDTO> getConversationDTO(int user1Id, int user2Id) {
        return toResponseDTOList(mesagesRepository.findConversation(user1Id, user2Id));
    }

    // DTO-based API
    public MessageResponseDTO getMessageResponseById(int id) {
        Mesages m = mesagesRepository.findById(id).orElse(null);
        if (m == null) return null;
        return toResponseDTO(m);
    }

    public MessageResponseDTO createMessage(MessageRequestDTO dto) {
        Users sender = usersRepository.findById(dto.getSenderUserId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + dto.getSenderUserId()));
        Users receiver = usersRepository.findById(dto.getRecipientUserId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + dto.getRecipientUserId()));

        Mesages entity = new Mesages();
        entity.setMessage(dto.getMessage());
        entity.setData(dto.getData());
        entity.setPin(dto.isPin());
        entity.setUser(sender);
        entity.setResever(receiver);

        Mesages saved = mesagesRepository.save(entity);
        return toResponseDTO(saved);
    }

    public MessageResponseDTO updateMessage(int id, MessageRequestDTO dto) {
        Mesages entity = mesagesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));

        Users sender = usersRepository.findById(dto.getSenderUserId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + dto.getSenderUserId()));
        Users receiver = usersRepository.findById(dto.getRecipientUserId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + dto.getRecipientUserId()));

        entity.setMessage(dto.getMessage());
        entity.setData(dto.getData());
        entity.setPin(dto.isPin());
        entity.setUser(sender);
        entity.setResever(receiver);

        Mesages saved = mesagesRepository.save(entity);
        return toResponseDTO(saved);
    }

    private MessageResponseDTO toResponseDTO(Mesages m) {
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessageId(m.getMessageId());
        res.setMessage(m.getMessage());
        res.setData(m.getData());
        res.setPin(m.isPin());
        if (m.getUser() != null) {
            res.setSenderId(m.getUser().getUserId());
        }
        if (m.getResever() != null) {
            res.setReceiverId(m.getResever().getUserId());
        }
        return res;
    }

    private List<MessageResponseDTO> toResponseDTOList(List<Mesages> list) {
        return list.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
