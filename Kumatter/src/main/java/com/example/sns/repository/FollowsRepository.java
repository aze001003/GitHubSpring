package com.example.sns.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sns.entity.Follows;
import com.example.sns.entity.Users;

/**
 * フォロー関係のDBアクセスを担うリポジトリ。
 * フォロー中かの判定や削除処理を提供する。
 *
 * @author 岡本
 * @since 2025-07-10
 */
public interface FollowsRepository extends JpaRepository<Follows, UUID> {
	/**
	 * ログインユーザーが指定ユーザーをフォローしているかを判定します。
	 *
	 * @param follower フォローしている側（ログインユーザー）
	 * @param followee フォローされている側（対象ユーザー）
	 * @return 存在すればtrue
	 */
	boolean existsByFollowerAndFollowee(Users follower, Users followee);
	/**
	 * フォロー関係を取得します。
	 *
	 * @param follower フォローしている側
	 * @param followee フォローされている側
	 * @return 該当するFollowsエンティティ
	 */
	Optional<Follows> findByFollowerAndFollowee(Users follower, Users followee);
	/**
	 * 指定したフォロー関係を削除します。
	 *
	 * @param follower フォローしている側
	 * @param followee フォローされている側
	 */
	void deleteByFollowerAndFollowee(Users follower, Users followee);
	/**
	 * 指定ユーザーがフォローしているユーザーID一覧を取得します。
	 *
	 * @param followerId フォロワー（ログインユーザー）のID
	 * @return フォローしているユーザー（followee）のID一覧
	 */
	@Query("SELECT f.followee.userId FROM Follows f WHERE f.follower.userId = :followerId")
	List<UUID> findFolloweeIdsByFollowerId(@Param("followerId") UUID followerId);
	/**
	 * 指定ユーザーをフォローしているユーザーID一覧を取得します。
	 *
	 * @param followeeId フォローされているユーザー（ログインユーザー）のID
	 * @return フォローしているユーザー（follower）のID一覧
	 */
	@Query("SELECT f.follower.userId FROM Follows f WHERE f.followee.userId = :followeeId")
	List<UUID> findFollowerIdsByFolloweeId(@Param("followeeId") UUID followeeId);
	
	int countByFollower_UserId(UUID userId);
	int countByFollowee_UserId(UUID userId);

}