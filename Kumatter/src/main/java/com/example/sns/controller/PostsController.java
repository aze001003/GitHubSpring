package com.example.sns.controller;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sns.dto.PostViewDto;
import com.example.sns.entity.Users;
import com.example.sns.repository.FollowsRepository;
import com.example.sns.repository.LikesRepository;
import com.example.sns.repository.PostsRepository;
import com.example.sns.security.UsersDetails;
import com.example.sns.service.PostsService;
/**
 * ホーム画面（投稿一覧＋投稿作成）を制御するコントローラークラス。
 * セッションからログインユーザーを取得し、投稿一覧の取得と新規投稿作成を行う。
 * 
 * @author 岡本
 * @since 2025-07-09
 */
@Controller
@RequestMapping("/users/home")
public class PostsController {
	private final PostsService postsService;
	
	public PostsController (
			PostsService postsService,
			FollowsRepository followsRepository,
			LikesRepository likesRepository,
			PostsRepository postsRepository) {
		this.postsService      = postsService;
	}
	/**
	 * ホーム画面表示（投稿一覧取得＋モデルセット）
	 * 
	 * @param loginUserDetails 認証済みユーザー情報
	 * @param mode 表示モード（"all": 全投稿, "followed": フォロー中ユーザーの投稿）
	 * @param model Viewへデータを渡すためのモデル
	 * @return home.htmlテンプレート名
	 */
	@GetMapping
	public String home(
			@AuthenticationPrincipal UsersDetails loginUserDetails,
			@RequestParam(name = "mode", required = false, defaultValue = "all") String mode,
			Model model) {
		if (loginUserDetails == null) return "redirect:/users/login";
		
		Users loginUser = loginUserDetails.getUser();
		List<PostViewDto> postDtoList;
		
		if ("followed".equals(mode)) {
			postDtoList = postsService.getFollowedUsersPostsWithLikes(loginUser);
		}else {
			postDtoList = postsService.getAllPostsWithLikes(loginUser);
		}
		model.addAttribute("user", loginUser);
		model.addAttribute("posts", postDtoList);
		model.addAttribute("mode", mode);
		return "home";
	}
	/**
	 * 新規投稿処理
	 * 
	 * @param loginUserDetails 認証済みユーザー情報
	 * @param content 投稿内容
	 * @param redirectAttributes フラッシュ属性で成功・失敗メッセージを渡す
	 * @return ホーム画面へリダイレクト
	 */
	@PostMapping("/posts/create")
	public String createPost(
			@AuthenticationPrincipal UsersDetails loginUserDetails,
			String content,
			RedirectAttributes redirectAttributes) {
		if (loginUserDetails == null) return "redirect:/users/login";
		try {
			postsService.createNormalPost(loginUserDetails.getUser(), content);
			redirectAttributes.addFlashAttribute("successMessage", "投稿が完了しました。");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/users/home";
	}
}