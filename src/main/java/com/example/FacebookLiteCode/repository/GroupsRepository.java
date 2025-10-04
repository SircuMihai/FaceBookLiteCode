package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupsRepository extends JpaRepository<Groups, Integer> {
    List<Groups> findByGroupNameContainingIgnoreCase(String groupName);
    List<Groups> findByPrivacy(String privacy);
    Groups findByGroupName(String groupName);
}
