package com.example.sns.security;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.example.sns.entity.Users;
/**
* Spring Securityの認証情報をセッションに構築・設定するヘルパークラス。
* コントローラーやサービスからの認証状態の設定を一元管理する。
* 
* @author 岡本
* @since 2025-07-08
*/
@Component
public class AuthenticationHelper {
	/**
	 * 指定されたユーザー情報をもとに認証情報を構築し、Spring Securityにセットする。
	 * 
	 * @param user 認証対象のユーザー
	 * @param request HTTPリクエスト（セッション取得用）
	 */
	public static void setAuthenticationForUser(Users user, HttpServletRequest request) {
		UsersDetails userDetails = new UsersDetails(user, user.getLoginId());
		// 認証トークンを生成（パスワード情報はnullにしている）
		UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		// 新規のセキュリティコンテキストを作成し、認証トークンを設定
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authToken);
		// Spring Securityのコンテキストにセット（スレッドローカル管理）
		SecurityContextHolder.setContext(context);
		// HTTPセッションにも認証情報を保存し、セッション管理を有効化
		request.getSession(true).setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				context);
	}
}