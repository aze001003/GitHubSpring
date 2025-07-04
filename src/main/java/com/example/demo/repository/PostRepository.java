package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;

//PostRepository.java
public interface PostRepository extends JpaRepository<Post, UUID> {
List<Post> findAllByOrderByCreatedAtDesc();
List<Post> findByUser(User user);
}