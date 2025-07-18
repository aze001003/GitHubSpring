package com.example.sns.service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.sns.dto.UserProfileDto;
import com.example.sns.dto.UserSuggestDto;
import com.example.sns.entity.Users;
import com.example.sns.repository.FollowsRepository;
import com.example.sns.repository.LikesRepository;
import com.example.sns.repository.PostsRepository;
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
	private final FollowsRepository followsRepository;
	private final LikesRepository likesRepository;
	private final PostsRepository postsRepository;
	private final PasswordEncoder passwordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
	/**
	 * コンストラクタによる依存性注入。
	 * 
	 * @param usersRepository Usersエンティティに対するリポジトリ
	 * @param passwordEncoder パスワードのハッシュ化に使用
	 */
	public UsersService(
			UsersRepository usersRepository,
			FollowsRepository followsRepository,
			LikesRepository likesRepository,
			PostsRepository postsRepository,
			PasswordEncoder passwordEncoder) {
		this.usersRepository   = usersRepository;
		this.followsRepository = followsRepository;
		this.likesRepository   = likesRepository;
		this.passwordEncoder   = passwordEncoder;
		this.postsRepository    = postsRepository;
	}

	/**
	 * ユーザー登録処理。
	 * 同一メールアドレスまたはログインIDの存在をチェックし、
	 * パスワードをハッシュ化した上で新規ユーザーを登録します。
	 *
	 * @param email メールアドレス
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
			String userBio) {
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
			logger.warn("認証失敗: ユーザーが見つかりません [{}]", loginIdOrEmail);
			return false;
		}
		Users user = userOpt.get();
		boolean match = passwordEncoder.matches(rawPassword, user.getPassword());
		logger.debug("パスワード一致: {}", match);
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
	/**
	 * ユーザー名またはログインIDの部分一致によるユーザー候補を取得します。
	 * 最大10件まで返します。
	 *
	 * @param query 検索文字列
	 * @return UserSuggestDtoのリスト
	 */
	public List<UserSuggestDto> searchUserSuggestions(String query, UUID loginUserId) {
		List<Users> users = usersRepository.findTop10ByLoginIdContainingIgnoreCaseOrUserNameContainingIgnoreCase(query, query);

		final Set<UUID> followedUserIds;
		final Set<UUID> followingUserIds;
		if (loginUserId != null) {
		followedUserIds = new HashSet<>(followsRepository.findFolloweeIdsByFollowerId(loginUserId));
		followingUserIds = new HashSet<>(followsRepository.findFollowerIdsByFolloweeId(loginUserId));
		} else {
		followedUserIds = new HashSet<>();
		followingUserIds = new HashSet<>();
		}
		return users.stream()
		.map(user -> {
		boolean followed = followedUserIds.contains(user.getUserId()); // 自分→相手
		boolean isSelf = loginUserId != null && loginUserId.equals(user.getUserId());
		boolean followingLoginUser = followingUserIds.contains(user.getUserId());  // 相手→自分
		return new UserSuggestDto(
				user.getUserId(),
				user.getUserName(),
				user.getLoginId(),
				followed,
				isSelf,
				followingLoginUser
			);
		})
		.collect(Collectors.toList());
	}
	
	public Users findByUserId(UUID userId) {
		return usersRepository.findById(userId).orElse(null);
	}
	/**
	 * 指定されたユーザーIDに対応するプロフィール情報を集約してDTOで返す。
	 * 
	 * - ユーザー基本情報（名前、自己紹介など）
	 * - 投稿数、フォロー数、フォロワー数、いいね数 を含む
	 * 
	 * @param userId プロフィール対象のユーザーID
	 * @return プロフィールDTO（UserProfileDto）／ユーザーが存在しない場合は null
	 */
	public UserProfileDto getUserProfileDto(UUID userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) return null;
		
		int postCount = postsRepository.countByUser_UserId(userId); // 任意：postsRepositoryでも可
		int followingCount = followsRepository.countByFollower_UserId(userId);
		int followerCount = followsRepository.countByFollowee_UserId(userId);
		int likedPostCount =(int)likesRepository.countByLikes_UserId(userId);
		
		return new UserProfileDto(
				user.getUserId(),
				user.getUserName(),
				user.getUserBio(),
				postCount,
				followingCount,
				followerCount,
				likedPostCount
			);
	}
}
