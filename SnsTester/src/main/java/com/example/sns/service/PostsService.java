package com.example.sns.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.dto.PostViewDto;
import com.example.sns.entity.PostType;
import com.example.sns.entity.Posts;
import com.example.sns.entity.Users;
import com.example.sns.repository.FollowsRepository;
import com.example.sns.repository.LikesRepository;
import com.example.sns.repository.PostsRepository;
import com.example.sns.util.RelativeTimeUtil;
/**
* 投稿情報のビジネスロジックを担うサービスクラス。
* コントローラー層とリポジトリ層の橋渡し役として、
* 投稿の作成、取得、拡散処理などを行う。
* 
* @author 岡本
* @since 2025-07-09
*/
@Service
public class PostsService {
	private final PostsRepository postsRepository;
	private final LikesRepository likesRepository;
	private final FollowsRepository followsRepository;
	
	
	public PostsService(
			PostsRepository postsRepository,
			LikesRepository likesRepository,
			FollowsRepository followsRepository) {
		this.postsRepository = postsRepository;
		this.likesRepository = likesRepository;
		this.followsRepository = followsRepository;
	}
	/**
	 * 通常投稿を作成・保存する。
	 *
	 * @param user 投稿を行うユーザー
	 * @param content 投稿内容（null・空文字不可）
	 * @return 保存された投稿エンティティ
	 */
	@Transactional
	public Posts createNormalPost(Users user, String content) {
		if (content == null || content.trim().isEmpty()) {
			throw new IllegalArgumentException("投稿内容を入力してください。");
		}
		Posts post = Posts.builder()
				.user(user)
				.content(content)
				.postType(PostType.NORMAL)
				.build();
		return postsRepository.save(post);
	}
	/**
	 * 指定したユーザーの投稿一覧を取得する（最新順）。
	 *
	 * @param user ユーザー
	 * @return 投稿一覧
	 */
	public List<Posts> getPostsByUser(Users user) {
		return postsRepository.findByUserOrderByCreatedAtDesc(user);
	}
	/**
	 * 投稿IDで投稿を1件取得する。
	 *
	 * @param postId 投稿ID
	 * @return 該当投稿（存在しない場合はnull）
	 */
	public Posts findById(UUID postId) {
		return postsRepository.findById(postId).orElse(null);
	}
	/**
	 * 全ユーザーの投稿一覧を取得する（最新順）。
	 * 
	 * @return 全投稿一覧
	 */
	public List<Posts> getAllPosts() {
		return postsRepository.findAllByOrderByCreatedAtDesc();
	}
	/**
	 * 指定したユーザーIDリストの投稿を取得し、いいね数や
	 * ログインユーザーのいいね済み判定を含めてDTOに変換して返す共通メソッド。
	 * -指定ユーザーの投稿を取得（最新順）
	 * -投稿IDリストを作成
	 * -投稿IDごとのいいね数を取得しMapに変換
	 * -ログインユーザーがいいね済みの投稿IDリストを取得
	 * - DTOに変換して返却
	 * 
	 * @param loginUser ログイン中のユーザー（nullの場合は未ログイン扱い）
	 * @param userIds 投稿対象のユーザーIDリスト
	 * @return 投稿DTOリスト（作成日時降順）
	 */
	private List<PostViewDto> getPostsByUserIdsWithLikes(Users loginUser, List<UUID> userIds) {
		if (userIds == null || userIds.isEmpty()) return List.of();
		List<Posts> posts = postsRepository.findByUser_UserIdInOrderByCreatedAtDesc(userIds);
		
		List<UUID> postIds = posts.stream()
				.map(Posts::getPostId)
				.collect(Collectors.toList());
		
		Map<UUID, Integer> likeCountMap = getLikeCountMap(postIds);
		
		List<UUID> likedPostIds = loginUser == null
				? List.of()
				: likesRepository.findPostIdsLikedByUser(loginUser.getUserId());
		return convertToPostViewDtoList(posts, likeCountMap, likedPostIds);
	}
	
	/**
	 * 全ユーザーの投稿を取得し、いいね数や
	 * ログインユーザーのいいね済み判定を含めてDTOに変換して返す。
	 * -投稿が存在する全ユーザーIDを取得
	 * -共通処理でDTOリストを取得
	 * 
	 * @param loginUser ログイン中のユーザー（nullの場合は未ログイン扱い）
	 * @return 全投稿のDTOリスト（作成日時降順）
	 */
	public List<PostViewDto> getAllPostsWithLikes(Users loginUser) {
		List<UUID> allUserIds = postsRepository.findAllUserIds();
		return getPostsByUserIdsWithLikes(loginUser, allUserIds);
	}
	/**
	 * フォロー中ユーザーと自分自身の投稿を取得し、いいね数や
	 * ログインユーザーのいいね済み判定を含めてDTOに変換して返す。
	 * -フォロー中ユーザーIDを取得
	 * -自分自身のIDも追加（自分の投稿も含めるため）
	 * -共通処理でDTOリストを取得
	 * 
	 * @param loginUser ログイン中のユーザー（nullの場合は未ログイン扱い）
	 * @return フォロー中ユーザー＋自分の投稿DTOリスト（作成日時降順）
	 */
	public List<PostViewDto> getFollowedUsersPostsWithLikes(Users loginUser) {
		if (loginUser == null) return List.of();
		List<UUID> followeeIds = followsRepository.findFolloweeIdsByFollowerId(loginUser.getUserId());
		followeeIds.add(loginUser.getUserId());
		return getPostsByUserIdsWithLikes(loginUser, followeeIds);
	}
	/**
	 * 指定した投稿IDリストに対するいいね数を集計し、投稿IDをキー、いいね数を値としたMapを返す。
	 * 
	 * @param postIds 投稿IDのリスト
	 * @return 投稿IDごとのいいね数マップ
	 */
	private Map<UUID, Integer> getLikeCountMap(List<UUID> postIds) {
		List<Object[]> results = likesRepository.countLikesByPostIds(postIds);
		Map<UUID, Integer> likeCountMap = new HashMap<>();
		for (Object[] row : results) {
			UUID postId = (UUID) row[0];
			Long count = (Long) row[1];
			likeCountMap.put(postId, count.intValue());
		}
		return likeCountMap;
	}
	/**
	 * 投稿エンティティリストをDTOリストに変換するユーティリティメソッド。
	 * @param posts 投稿エンティティのリスト
	 * @param likeCountMap 投稿IDごとのいいね数マップ
	 * @param likedPostIds ログインユーザーがいいね済みの投稿IDリスト
	 * @return 投稿DTOのリスト（相対時間付き）
	 */
	private List<PostViewDto> convertToPostViewDtoList(
			List<Posts> posts,
			java.util.Map<UUID, Integer> likeCountMap,
			List<UUID> likedPostIds) {
		return posts.stream()
				.map(post -> new PostViewDto(
						post.getPostId(),
						post.getContent(),
						RelativeTimeUtil.toRelativeTime(post.getCreatedAt()),
						post.getUser().getUserName(),
						post.getUser().getLoginId(),
						likeCountMap.getOrDefault(post.getPostId(), 0),
						likedPostIds.contains(post.getPostId())
					))
				.collect(Collectors.toList());
	}
}