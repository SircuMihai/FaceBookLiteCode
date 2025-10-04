package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Groups;
import com.example.FacebookLiteCode.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupsService {
    
    @Autowired
    private GroupsRepository groupsRepository;
    
    public List<Groups> getAllGroups() {
        return groupsRepository.findAll();
    }
    
    public Optional<Groups> getGroupById(int id) {
        return groupsRepository.findById(id);
    }
    
    public Groups saveGroup(Groups group) {
        return groupsRepository.save(group);
    }
    
    public void deleteGroup(int id) {
        groupsRepository.deleteById(id);
    }
    
    public List<Groups> getGroupsByName(String groupName) {
        return groupsRepository.findByGroupNameContainingIgnoreCase(groupName);
    }
    
    public List<Groups> getGroupsByPrivacy(String privacy) {
        return groupsRepository.findByPrivacy(privacy);
    }
    
    public Groups getGroupByName(String groupName) {
        return groupsRepository.findByGroupName(groupName);
    }
}
