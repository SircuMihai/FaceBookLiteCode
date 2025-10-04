package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Groups;
import com.example.FacebookLiteCode.model.Mesages;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesagesRepository extends JpaRepository<Mesages, Integer> {
    List<Mesages> findByUser(Users user);
    List<Mesages> findByGroup(Groups group);
    List<Mesages> findByUserUserId(int userId);
    List<Mesages> findByGroupGroupId(int groupId);
    List<Mesages> findByIsPin(boolean isPin);
    List<Mesages> findByMessageContainingIgnoreCase(String message);
}
