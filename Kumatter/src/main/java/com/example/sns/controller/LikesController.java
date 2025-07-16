package com.example.sns.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sns.security.UsersDetails;
import com.example.sns.service.LikesService;
/**
 * いいね機能を制御するコントローラークラス。
 * 投稿への「いいね」追加処理を担当し、リクエストを受けて
 * LikesServiceを呼び出してビジネスロジックを実行する。
 * 
 * ログインユーザーの取得や認証状態のチェックも行う。
 * 処理結果に応じてメッセージをリダイレクト先にフラッシュ属性で渡す。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@Controller
public class LikesController {
	private final LikesService likesService;
	/**
	 * LikesServiceのコンストラクタインジェクション。
	 * 
	 * @param likesService いいね機能のサービスクラス
	 */
	public LikesController(LikesService likesService) {
		this.likesService = likesService;
	}
	/**
	 * 投稿に対していいねを追加するPOSTリクエストのハンドラ。
	 * 
	 * @param postId いいねを付ける投稿のID（UUID文字列）
	 * @param redirectAttributes リダイレクト時にメッセージを渡すためのオブジェクト
	 * @return ログインしていなければログイン画面へ、それ以外はホーム画面へリダイレクト
	 */
	@PostMapping("/likes/add")
	public String addLike(
			  @RequestParam("postId") String postId,
			  @AuthenticationPrincipal UsersDetails loginUserDetails,
			  RedirectAttributes redirectAttributes) {
		if (loginUserDetails == null) return "redirect:/users/login";
		try {
			likesService.addLike(loginUserDetails.getUser().getUserId(), postId);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "いいねに失敗しました");
		}
		return "redirect:/users/home";
	}
}
