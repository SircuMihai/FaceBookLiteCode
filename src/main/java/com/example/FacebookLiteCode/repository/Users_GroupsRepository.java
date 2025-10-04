package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Users_Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Users_GroupsRepository extends JpaRepository<Users_Groups, Integer> {
    List<Users_Groups> findByUserId(int userId);
    List<Users_Groups> findByGroupId(int groupId);
    List<Users_Groups> findByGroupAdmin(boolean groupAdmin);
    Users_Groups findByUserIdAndGroupId(int userId, int groupId);
    List<Users_Groups> findByUserIdAndGroupAdmin(int userId, boolean groupAdmin);
}
