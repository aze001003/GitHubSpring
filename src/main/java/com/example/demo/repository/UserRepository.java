package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

//UserRepository.java
public interface UserRepository extends JpaRepository<User, UUID> {
 Optional<User> findByLoginId(String loginId);
}

