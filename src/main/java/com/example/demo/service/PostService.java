package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;

//PostService.java
@Service
public class PostService {
 @Autowired
 private PostRepository postRepository;

 public List<Post> getTimeline() {
     return postRepository.findAllByOrderByCreatedAtDesc();
 }

 public Post createPost(User user, String content) {
     Post post = new Post();
     post.setUser(user);
     post.setContent(content);
     post.setCreatedAt(LocalDateTime.now());
     post.setUpdatedAt(LocalDateTime.now());
     return postRepository.save(post);
 }
}
