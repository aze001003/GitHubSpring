package com.example.sns.service;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.entity.PostType;
import com.example.sns.entity.Posts;
import com.example.sns.entity.Users;
import com.example.sns.repository.PostsRepository;
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
	
	public PostsService(PostsRepository postsRepository) {
		this.postsRepository = postsRepository;
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
				// UUIDは自動生成に任せるなら不要。手動設定なら外すか @GeneratedValue削除
				//.postId(UUID.randomUUID())
				.user(user)
				.content(content)
				.postType(PostType.NORMAL)
				// createdAt, updatedAtは@PrePersistで自動設定されるので明示設定不要
				//.createdAt(LocalDateTime.now())
				//.updatedAt(LocalDateTime.now())
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
}