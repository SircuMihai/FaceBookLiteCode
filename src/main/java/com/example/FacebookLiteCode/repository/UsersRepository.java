package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    
    @Query("SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Users> findByEmailIgnoreCase(@Param("email") String email);
    
    List<Users> findByPrivateAccount(boolean privateAccount);
    List<Users> findByFirstNameContainingIgnoreCase(String firstName);
    List<Users> findByLastNameContainingIgnoreCase(String lastName);
}
