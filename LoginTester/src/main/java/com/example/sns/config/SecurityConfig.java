package com.example.sns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Securityの設定クラス。
 * ログイン画面、ログアウト、認可（アクセス制御）、
 * 認証プロバイダ、パスワードエンコーダなどをここで構成します。
 * 
 * @author 岡本
 * @since 2025-07-07
 */
@Configuration
public class SecurityConfig {
	/**
	 * セキュリティのルールを定義するメインの設定。
	 * 以下の設定を含みます：
	 * - 特定パス（ログイン、登録、静的リソース）へのアクセスは常に許可
	 * - それ以外のリクエストはすべて認証が必要
	 * - カスタムログイン画面の利用とパラメータ名の指定
	 * - ログアウト処理とリダイレクト先の指定
	 * 
	 * @param http Spring SecurityのHTTPセキュリティビルダー
	 * @return 構成済みのSecurityFilterChainインスタンス
	 * @throws Exception セキュリティ設定に失敗した場合にスローされる例外
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		/**
		 * リクエストに対するアクセス制御を定義
		 * -ログイン・登録画面と静的ファイルは常にアクセス許可
		 * -その他すべてのURLは認証が必要
		 */
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/users/login", "/users/register", "/css/**", "/js/**").permitAll()
				.anyRequest().authenticated()
		)
		// フォームログイン設定
		.formLogin(form -> form
				.loginPage("/users/login")                // カスタムログインページのパス（GET）
				.loginProcessingUrl("/users/login")       // 認証処理を実行するパス（POST）
				.usernameParameter("loginIdOrEmail")      // ログインフォームの「ユーザーID/メールアドレス」入力欄のname属性
				.passwordParameter("password")            // ログインフォームのパスワード欄のname属性
				.defaultSuccessUrl("/users/home", true)   //  認証成功時に遷移するデフォルトのページ
				.permitAll()                              // ログイン画面のアクセスは常に許可
		)
		// ログアウト設定
		.logout(logout -> logout
				.logoutUrl("/users/logout")               // ログアウト処理を行うURL
				.logoutSuccessUrl("/users/login?logout")  // ログアウト成功後にリダイレクトするURL
				.permitAll()                              // ログアウトは常にアクセス許可
		);
		return http.build();
	}

	/**
	 * パスワードのエンコード方式としてBCryptを使用するエンコーダーを定義。
	 * 主にユーザー登録時とログイン時のパスワードチェックに使用される。
	 * 
	 * @return BCryptPasswordEncoderのインスタンス
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * データベースに基づいた認証を行うDaoAuthenticationProviderを定義。
	 * UserDetailsServiceとPasswordEncoderを使用して認証処理を構成する。
	 * 
	 * @param userDetailsService 認証に使用するユーザー情報の取得サービス
	 * @param passwordEncoder パスワードのハッシュ化および照合に使用するエンコーダー
	 * @return 構成済みのDaoAuthenticationProvider
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}
}
