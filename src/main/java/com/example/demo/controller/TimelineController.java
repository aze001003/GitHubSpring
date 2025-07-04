package com.example.demo.controller;

//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.PostService;

//TimelineController.java
@Controller
public class TimelineController {
 @Autowired
 private PostService postService;

 @GetMapping("/")
 public String timeline(Model model) {
     model.addAttribute("posts", postService.getTimeline());
     return "timeline";
 }

 @PostMapping("/post")
 public String createPost(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String content) {
     // ログインユーザー取得
     //User user = UserService.findByLoginId(userDetails.getUsername()).orElseThrow();
     //postService.createPost(user, content);
     return "redirect:/";
 }
}

