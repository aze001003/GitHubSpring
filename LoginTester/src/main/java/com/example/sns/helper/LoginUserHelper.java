package com.example.sns.helper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.sns.entity.Users;
import com.example.sns.security.UsersDetails;
/**
* 現在ログイン中のユーザー情報を取得するための共通ヘルパークラス。
* コントローラーやサービス層での重複を避け、認証状態の取得を一元化する。
*
* @author 岡本
* @since 2025-07-09
*/
public class LoginUserHelper {
	/**
	 * 現在ログインしているユーザーを取得します。
	 * 認証されていない場合は null を返します。
	 *
	 * @return ログイン中のユーザー情報（Usersエンティティ）または null
	 */
	public static Users getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof UsersDetails)) return null;
		return ((UsersDetails) auth.getPrincipal()).getUser();
	}
	/**
	 * ログイン中かどうかを判定します。
	 *
	 * @return ログイン中なら true、そうでなければ false
	 */
	public static boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated()
				&& auth.getPrincipal() instanceof UsersDetails;
	}
}
