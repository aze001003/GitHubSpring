package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

//UserService.java
@Service
public class UserService {
 @Autowired
 private UserRepository userRepository;

 public Optional<User> findByLoginId(String loginId) {
     return userRepository.findByLoginId(loginId);
 }

 public User register(User user) {
     user.setCreatedAt(LocalDateTime.now());
     user.setUpdatedAt(LocalDateTime.now());
     // パスワードはBCryptでハッシュ化
     user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
     System.out.println("a");
     return userRepository.save(user);
 }
}
//エラー発生する可能性あり