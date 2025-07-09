package com.example.sns.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sns.entity.Posts;
import com.example.sns.entity.Users;
/**
* 投稿情報にアクセスするためのリポジトリインタフェース。
* Spring Data JPA により、基本的なCRUD操作が自動生成される。
*
* @author 岡本
* @since 2025-07-09
*/
@Repository
public interface PostsRepository extends JpaRepository<Posts, UUID> {
	/**
	 * 指定したユーザーに紐づく全投稿を作成日時の降順で取得する。
	 *
	 * @param user 投稿を作成したユーザー
	 * @return 投稿一覧（最新順）
	 */
	List<Posts> findByUserOrderByCreatedAtDesc(Users user);
	/**
	 * 全ユーザーの投稿を作成日時の降順で取得する。
	 * 
	 * @return 投稿一覧（最新順）
	 */
	List<Posts> findAllByOrderByCreatedAtDesc();
}