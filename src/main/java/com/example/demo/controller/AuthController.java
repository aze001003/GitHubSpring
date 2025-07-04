package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

//AuthController.java
@Controller
public class AuthController {
 @Autowired
 private UserService userService;

 @GetMapping("/login")
 public String login() {
     return "login";
 }

 @GetMapping("/register")
 public String registerForm(Model model) {
     model.addAttribute("user", new User());
     return "register";
 }

 @PostMapping("/register")
 public String register(@ModelAttribute User user) {
     userService.register(user);
     return "redirect:/login";
 }
}

