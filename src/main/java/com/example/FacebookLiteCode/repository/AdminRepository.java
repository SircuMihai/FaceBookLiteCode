package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByName(String name);
    Admin findByNameAndPassword(String name, String password);
}
