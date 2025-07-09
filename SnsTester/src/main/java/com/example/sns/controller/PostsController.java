package com.example.sns.controller;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sns.dto.PostViewDto;
import com.example.sns.entity.Posts;
import com.example.sns.entity.Users;
import com.example.sns.helper.LoginUserHelper;
import com.example.sns.helper.TimeAgoHelper;
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
	
	public PostsController(PostsService postsService) {
		this.postsService = postsService;
	}
	/**
	 * ホーム画面表示（投稿一覧取得＋モデルセット）
	 * 
	 * @param session セッションからログインユーザーを取得
	 * @param model Viewへデータを渡すためのモデル
	 * @return home.htmlテンプレート名
	 */
	@GetMapping
	public String home(HttpSession session, Model model) {
		Users loginUser = LoginUserHelper.getLoginUser();
		if (loginUser == null) {
			return "redirect:/users/login";
		}
		model.addAttribute("user", loginUser);
		// 投稿一覧も追加
		List<Posts> postList = postsService.getAllPosts();
		 List<PostViewDto> postDtoList = convertToPostViewDtoList(postList);
		model.addAttribute("posts", postDtoList);
		return "home";
	}
	
	/**
	 * 新規投稿処理
	 * 
	 * @param session セッションからログインユーザーを取得
	 * @param content 投稿内容
	 * @param redirectAttributes フラッシュ属性で成功・失敗メッセージを渡す
	 * @return ホーム画面へリダイレクト
	 */
	@PostMapping("/posts/create")
	public String createPost(HttpSession session, String content, RedirectAttributes redirectAttributes) {
		Users loginUser = LoginUserHelper.getLoginUser();
		if (loginUser == null) {
			return "redirect:/users/login";
		}
		try {
			postsService.createNormalPost(loginUser, content);
			redirectAttributes.addFlashAttribute("successMessage", "投稿が完了しました。");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/users/home";
	}
	/**
	 * 投稿エンティティリストをDTOリストに変換するユーティリティメソッド。
	 * @param posts 投稿エンティティのリスト
	 * @return 投稿DTOのリスト（相対時間付き）
	 */
	private List<PostViewDto> convertToPostViewDtoList(List<Posts> posts) {
		return posts.stream()
				.map(post -> new PostViewDto(
						post.getPostId(),
						post.getContent(),
						TimeAgoHelper.toRelativeTime(post.getCreatedAt()),
						post.getUser().getUserName()
						))
				.collect(Collectors.toList());
	}
}