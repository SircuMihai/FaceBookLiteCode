package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Mesages;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesagesRepository extends JpaRepository<Mesages, Integer> {
    List<Mesages> findByUser(Users user);
    List<Mesages> findByUserUserId(int userId);
    List<Mesages> findByResever(Users resever);
    List<Mesages> findByReseverUserId(int reseverId);
    List<Mesages> findByIsPin(boolean isPin);
    List<Mesages> findByMessageContainingIgnoreCase(String message);

    @Query("SELECT m FROM Mesages m WHERE (m.user.userId = :user1 AND m.resever.userId = :user2) OR (m.user.userId = :user2 AND m.resever.userId = :user1) ORDER BY m.data ASC")
    List<Mesages> findConversation(@Param("user1") int user1Id, @Param("user2") int user2Id);
}
