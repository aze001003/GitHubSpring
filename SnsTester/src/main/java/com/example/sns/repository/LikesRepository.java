package com.example.sns.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sns.entity.Likes;
import com.example.sns.entity.Posts;
import com.example.sns.entity.Users;

/**
 * Likesエンティティに対するデータアクセスを行うリポジトリインターフェース。
 * JpaRepositoryを継承し、基本的なCRUD操作は自動実装される。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
public interface LikesRepository extends JpaRepository<Likes, UUID> {
	/**
	 * 指定ユーザーが指定投稿に対して既にいいねしているか判定する。
	 * 
	 * @param user 判定対象のユーザーエンティティ
	 * @param post 判定対象の投稿エンティティ
	 * @return いいね済みならtrue、そうでなければfalse
	 */
	boolean existsByUserAndPost(Users user, Posts post);
	/**
	 * 複数の投稿IDに対して、それぞれの投稿のいいね数を取得するJPQLクエリ。
	 * グルーピングして投稿IDごとの件数を返す。
	 * 
	 * @param postIds 投稿IDのリスト
	 * @return Object配列のリスト。各配列は[投稿ID, いいね数]を表す。
	 */
	@Query("SELECT l.post.postId, COUNT(l) FROM Likes l WHERE l.post.postId IN :postIds GROUP BY l.post.postId")
	List<Object[]> countLikesByPostIds(@Param("postIds") List<UUID> postIds);
	/**
	 * 指定ユーザーがいいね済みの投稿ID一覧を取得するJPQLクエリ。
	 * 
	 * @param userId ユーザーID(UUID)
	 * @return いいね済み投稿IDのリスト
	 */
	@Query("SELECT l.post.postId FROM Likes l WHERE l.user.userId = :userId")
	List<UUID> findPostIdsLikedByUser(@Param("userId") UUID userId);
	/**
	 * 指定投稿のいいね数をカウントする。
	 * 
	 * @param postId 投稿ID(UUID)
	 * @return いいねの件数
	 */
	int countByPost_PostId(UUID postId);
	/**
	 * 指定ユーザーと投稿に紐づくいいねを削除する。
	 * 
	 * @param user ユーザーエンティティ
	 * @param post 投稿エンティティ
	 */
	void deleteByUserAndPost(Users user, Posts post);
}
