package com.example.sns.security;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.sns.entity.Users;
import com.example.sns.repository.UsersRepository;
/**
 * Spring Securityの認証時にユーザー情報をデータベースから取得するサービス。
 * UserDetailsServiceインターフェースの実装。
 * 
 * @author 岡本
 * @since 2025-07-07
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UsersRepository usersRepository;
	
	public CustomUserDetailsService(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}
	/**
	 * ログインIDまたはメールアドレスを使ってユーザーを検索し、
	 * 見つかればUsersDetailsにラップして返す。
	 * 見つからなければUsernameNotFoundExceptionをスローする。
	 *
	 * @param loginIdOrEmail ログインIDまたはメールアドレス
	 * @return UserDetails 実装オブジェクト
	 * @throws UsernameNotFoundException ユーザーが見つからない場合
	 */
	@Override
	public UserDetails loadUserByUsername(String loginIdOrEmail) throws UsernameNotFoundException {
		Optional<Users> optionalUser =
				usersRepository.findByLoginIdOrEmail(loginIdOrEmail, loginIdOrEmail);
		Users user = optionalUser.orElseThrow(
				() -> new UsernameNotFoundException("ユーザーが見つかりません: " + loginIdOrEmail)
				);
		// UsersDetailsのコンストラクタがユーザー情報だけを受け取る場合、
		// 第二引数は不要なので削除しています。
		return new UsersDetails(user, loginIdOrEmail);  // ← 修正ポイント（第二引数は不要）
	}
}