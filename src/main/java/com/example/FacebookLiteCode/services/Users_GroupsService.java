package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Users_Groups;
import com.example.FacebookLiteCode.repository.Users_GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Users_GroupsService {
    
    @Autowired
    private Users_GroupsRepository users_GroupsRepository;
    
    public List<Users_Groups> getAllUsersGroups() {
        return users_GroupsRepository.findAll();
    }
    
    public Optional<Users_Groups> getUsersGroupsById(int id) {
        return users_GroupsRepository.findById(id);
    }
    
    public Users_Groups saveUsersGroups(Users_Groups usersGroups) {
        return users_GroupsRepository.save(usersGroups);
    }
    
    public void deleteUsersGroups(int id) {
        users_GroupsRepository.deleteById(id);
    }
    
    public List<Users_Groups> getUsersGroupsByUserId(int userId) {
        return users_GroupsRepository.findByUserId(userId);
    }
    
    public List<Users_Groups> getUsersGroupsByGroupId(int groupId) {
        return users_GroupsRepository.findByGroupId(groupId);
    }
    
    public List<Users_Groups> getUsersGroupsByGroupAdmin(boolean groupAdmin) {
        return users_GroupsRepository.findByGroupAdmin(groupAdmin);
    }
    
    public Users_Groups getUsersGroupsByUserIdAndGroupId(int userId, int groupId) {
        return users_GroupsRepository.findByUserIdAndGroupId(userId, groupId);
    }
    
    public List<Users_Groups> getUsersGroupsByUserIdAndGroupAdmin(int userId, boolean groupAdmin) {
        return users_GroupsRepository.findByUserIdAndGroupAdmin(userId, groupAdmin);
    }
}
