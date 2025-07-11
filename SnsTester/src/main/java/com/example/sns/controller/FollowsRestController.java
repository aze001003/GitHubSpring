package com.example.sns.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sns.entity.Users;
import com.example.sns.service.FollowsService;
import com.example.sns.service.UsersService;

/**
 * フォロー／フォロー解除機能を提供するRESTコントローラー。
 * ログインユーザーが他のユーザーをフォローおよびフォロー解除できるAPIを提供する。
 *
 * CSRF対策はHTMLのmetaタグおよびJavaScriptのfetchヘッダーで対応済み。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/api/follows")
public class FollowsRestController {
	private final FollowsService followsService;
	private final UsersService usersService;
	
	public FollowsRestController(FollowsService followsService, UsersService usersService) {
		this.followsService = followsService;
		this.usersService = usersService;
	}
	/**
	 * 指定したユーザーをログインユーザーがフォローする。
	 * 
	 * @param userId フォロー対象のユーザーID（PathVariableで受け取る）
	 * @param principal ログイン中のユーザー情報（認証済みのユーザー）
	 * @return フォロー成功時は200 OK、エラー時は適切なステータスとメッセージを返す
	 */
	@PostMapping("/{userId}")
	public ResponseEntity<?> follow(@PathVariable UUID userId, Principal principal) {
		System.out.println("フォローボタンが押された");
		Users follower = usersService.findByLoginIdOrEmail(principal.getName());
		if (follower == null) {
			return ResponseEntity.status(401).body("ログインユーザーが存在しません");
		}
		if (follower.getUserId().equals(userId)) {
			return ResponseEntity.badRequest().body("自分自身はフォローできません");
		}
		followsService.follow(follower.getUserId(), userId);
		return ResponseEntity.ok().build();
	}
	/**
	 * 指定したユーザーのフォローをログインユーザーが解除する。
	 * 
	* @param userId フォロー解除対象のユーザーID（PathVariableで受け取る）
	* @param principal ログイン中のユーザー情報（認証済みのユーザー）
	* @return フォロー解除成功時は200 OK、エラー時は適切なステータスとメッセージを返す
	*/
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> unfollow(@PathVariable UUID userId, Principal principal) {
		Users follower = usersService.findByLoginIdOrEmail(principal.getName());
		if (follower == null) {
			return ResponseEntity.status(401).body("ログインユーザーが存在しません");
		}
		if (follower.getUserId().equals(userId)) {
			return ResponseEntity.badRequest().body("自分自身のフォロー解除は不要です");
		}
		followsService.unfollow(follower.getUserId(), userId);
		return ResponseEntity.ok().build();
	}
}