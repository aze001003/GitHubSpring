package com.example.sns.service;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sns.entity.Users;
import com.example.sns.repository.UsersRepository;
/**
 * ユーザー登録・認証に関するビジネスロジックを担当するサービスクラス。
 * コントローラー層とリポジトリ層の中間に位置し、主にバリデーションやパスワード処理などを行う。
 *
 * @author 岡本
 * @since 2025-07-07
 */
@Service
public class UsersService {
	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;
	/**
	 * コンストラクタによる依存性注入。
	 * 
	 * @param usersRepository Usersエンティティに対するリポジトリ
	 * @param passwordEncoder パスワードのハッシュ化に使用
	 */
	public UsersService(
			UsersRepository usersRepository,
			PasswordEncoder passwordEncoder
			) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * ユーザー登録処理。
	 * 同一メールアドレスまたはログインIDの存在をチェックし、
	 * パスワードをハッシュ化した上で新規ユーザーを登録します。
	 *
	 * @param email メールアドレス
	 * @param loginId ログインID
	 * @param userName ユーザー名
	 * @param rawPassword 平文パスワード
	 * @param userBio 自己紹介（任意）
	 * @return 登録されたユーザー情報
	 * @throws IllegalArgumentException 既に同じemailまたはloginIdが存在する場合
	 */
	public Users registerUser(
			String email,
			String loginId,
			String userName,
			String rawPassword,
			String userBio
			) {
		if (usersRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("このメールアドレスはすでに登録されています");
		}
		if (usersRepository.existsByLoginId(loginId)) {
			throw new IllegalArgumentException("このログインIDはすでに使用されています");
		}
		
		Users user = new Users();
		user.setEmail(email);
		user.setLoginId(loginId);
		user.setUserName(userName);
		user.setUserBio(userBio);
		user.setPassword(passwordEncoder.encode(rawPassword)); // パスワードはハッシュ化して保存
		LocalDateTime now = LocalDateTime.now();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		
		return usersRepository.save(user);
}
	/**
	 * ログイン認証処理。
	 * 入力されたログインIDまたはメールアドレスに一致するユーザーを検索し、
	 * パスワードが正しいかどうかを検証します。
	 *
	 * @param loginIdOrEmail 入力されたログインIDまたはメールアドレス
	 * @param rawPassword 入力された平文パスワード
	 * @return 認証成功ならtrue、失敗ならfalse
	 */
	public boolean authenticate(String loginIdOrEmail, String rawPassword) {
		Optional<Users> userOpt =
				usersRepository.findByLoginIdOrEmail(loginIdOrEmail, loginIdOrEmail);
		if (userOpt.isEmpty()) {
			System.out.println("ユーザーが見つかりませんでした");
			return false;
		}
		Users user = userOpt.get();
		// パスワード比較結果をログに出す（開発用、運用時はログレベル注意）
		boolean match = passwordEncoder.matches(rawPassword, user.getPassword());
		System.out.println("パスワード一致: " + match);
		return match;
	}

	/**
	 * ログインIDまたはメールアドレスによりユーザー情報を取得します。
	 *
	 * @param loginIdOrEmail ログインIDまたはメールアドレス
	 * @return 一致するユーザーが存在すればUsers、存在しなければnull
	 */
	public Users findByLoginIdOrEmail(String loginIdOrEmail) {
		return usersRepository.findByLoginIdOrEmail(loginIdOrEmail, loginIdOrEmail)
				.orElse(null);
	}
}
