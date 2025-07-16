package com.example.sns.service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.sns.entity.Users;
import com.example.sns.security.AuthenticationHelper;
/**
* ユーザーの認証・ログイン・ログアウト関連処理をまとめたサービスクラス。
* Controllerからの認証関連処理を一元管理し、単一責任の原則に沿って設計する。
* 
* @author 岡本
* @since 2025-07-09
*/
@Service
public class AuthenticationService {
	/**
	 * ログイン処理。
	 * ユーザーをセッションに保存し、Spring Securityの認証情報を設定する。
	 *
	 * @param user 認証済みユーザー情報
	 * @param session HTTPセッション
	 * @param request HTTPリクエスト
	 */
	public void loginUser(Users user, HttpSession session, HttpServletRequest request) {
		session.setAttribute("loginUser", user);
		AuthenticationHelper.setAuthenticationForUser(user, request);
	}
	/**
	 * ログアウト処理。
	 * - セッションを無効化
	 * -Spring Securityの認証情報を明示的にクリア
	 * @param session 現在のHTTPセッション（破棄対象）
	 */
	public void logoutUser(HttpSession session) {
		if (session != null) session.invalidate();
		SecurityContextHolder.clearContext();
	}
}