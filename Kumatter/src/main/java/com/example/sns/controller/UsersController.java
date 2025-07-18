package com.example.sns.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.sns.dto.UserProfileDto;
import com.example.sns.entity.Users;
import com.example.sns.repository.FollowsRepository;
import com.example.sns.repository.LikesRepository;
import com.example.sns.repository.PostsRepository;
import com.example.sns.security.UsersDetails;
import com.example.sns.service.PostsService;
import com.example.sns.service.UsersService;

/**
 * ユーザー情報表示や編集、フォロー機能など
 * ユーザー関連の画面表示を担当するコントローラークラス。
 * 
 * @author 岡本
 * @since 2025-07-16
 */
@Controller
public class UsersController {
	private final UsersService usersService;
	private final PostsService postsService;
	private final FollowsRepository followsRepository;
	
	public UsersController(
			UsersService usersService, 
			PostsService postsService, 
			FollowsRepository followsRepository,
			LikesRepository likesRepository,
			PostsRepository postsRepository) {
		this.usersService = usersService;
		this.postsService = postsService;
		this.followsRepository = followsRepository;
	}
	/**
	 * 指定されたユーザーIDのプロフィールページを表示する。
	 * 
	 * @param userId URLパスのユーザーID（UUID）
	 * @param model Viewに渡すモデル
	 * @return ユーザープロフィール画面のテンプレート名（例: "user_profile"）
	 */
	@GetMapping("/users/{userId}")
	public String showUserProfile(
			@PathVariable("userId") UUID userId, 
			@AuthenticationPrincipal UsersDetails loginUserDetails,
			Model model) {
		if (loginUserDetails == null)  return "redirect:/users/login"; 
		
		Users user = usersService.findByUserId(userId);
		if (user == null) return "error/404";
		
		Users loginUser = loginUserDetails.getUser();
		UserProfileDto profileDto = usersService.getUserProfileDto(userId);
		
		model.addAttribute("user",  profileDto);
		model.addAttribute("posts", postsService.getPostsByUserWithLikes(loginUser, user));
		model.addAttribute("isOwnProfile", loginUser.getUserId().equals(user.getUserId()));
		model.addAttribute("isFollowing", followsRepository.existsByFollowerAndFollowee(loginUser, user));
		return "user_profile";
	}
}