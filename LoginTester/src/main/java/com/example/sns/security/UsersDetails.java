package com.example.sns.security;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.sns.entity.Users;

/**
 * Spring Securityがユーザー情報を扱うためのUserDetails実装クラス。
 * 認証に使用されたログインIDまたはメールアドレスを保持し、
 * 認証後もユーザー情報にアクセスできるようにします。
 *
 * @author 岡本
 * @since 2025-07-07
 */
public class UsersDetails implements UserDetails {
	private final Users user;  // 実際のユーザーデータを保持
	private final String usernameForAuth;  // 認証に使われたユーザー名（ログインIDかメールアドレス）
	/**
	 * コンストラクタ
	 * 
	 * @param user 実際のユーザーエンティティ
	 * @param usernameForAuth 認証に使用された文字列（ログインIDまたはメールアドレス）
	 */
	public UsersDetails(Users user, String usernameForAuth) {
		this.user = user;
		this.usernameForAuth = usernameForAuth;
	}
	/**
	 * ユーザーIDの取得（アプリ内で必要な場合に利用）
	 */
	public UUID getUserId() {
		return user.getUserId();
	}
	/**
	 * 権限情報を返却（固定でROLE_USERを付与）
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	/**
	 * ハッシュ化されたパスワードを返却
	 */
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	/**
	 * 認証に使用されたユーザー名（ログインIDまたはメールアドレス）を返却
	 */
	@Override
	public String getUsername() {
		return usernameForAuth;  // ログインID or email（入力値）をそのまま返す
	}
	/**
	 * アカウントの有効期限チェック（常にtrueで有効）
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	/**
	 * アカウントのロック状態チェック（常にtrueでロックされていない）
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	/**
	 * 資格情報（パスワード）の有効期限チェック（常にtrueで有効）
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	/**
	 * アカウントが有効かどうか（常にtrueで有効
	 * */
	@Override
	public boolean isEnabled() {
		return true;
	}
	/** 
	 * 認証後にアプリ内でUsersエンティティを使いたい場合のゲッター
	 */
	public Users getUser() {
		return user;
	}
}