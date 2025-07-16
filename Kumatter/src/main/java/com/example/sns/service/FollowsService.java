package com.example.sns.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.entity.Follows;
import com.example.sns.entity.Users;
import com.example.sns.repository.FollowsRepository;
import com.example.sns.repository.UsersRepository;

/**
 * フォロー・フォロー解除のビジネスロジックを提供するサービスクラス。
 * Followsエンティティを操作する。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@Service
public class FollowsService {
	private final FollowsRepository followsRepository;
	private final UsersRepository usersRepository;
	
	public FollowsService(FollowsRepository followsRepository, UsersRepository usersRepository) {
		this.followsRepository = followsRepository;
		this.usersRepository = usersRepository;
	}
	/**
	 * 指定ユーザーをフォローする。
	 * 
	 * @param followerId フォロワーのユーザーID
	 * @param followeeId フォローされる側のユーザーID
	 */
	@Transactional
	public void follow(UUID followerId, UUID followeeId) {
		Users follower = usersRepository.findById(followerId)
				.orElseThrow(() -> new IllegalArgumentException("フォロワーが存在しません"));
		Users followee = usersRepository.findById(followeeId)
				.orElseThrow(() -> new IllegalArgumentException("フォロー対象が存在しません"));
		if (followsRepository.existsByFollowerAndFollowee(follower, followee)) {
			return; // 既にフォロー済みなら何もしない
		}
		Follows follow = new Follows();
		follow.setFollower(follower);
		follow.setFollowee(followee);
		follow.setCreatedAt(LocalDateTime.now());
		followsRepository.save(follow);
	}
	/**
	 * 指定ユーザーをフォロー解除する。
	 * 
	 * @param followerId フォロワーのユーザーID
	 * @param followeeId フォローされる側のユーザーID
	 */
	@Transactional
	public void unfollow(UUID followerId, UUID followeeId) {
		Users follower = usersRepository.findById(followerId)
				.orElseThrow(() -> new IllegalArgumentException("フォロワーが存在しません"));
		Users followee = usersRepository.findById(followeeId)
				.orElseThrow(() -> new IllegalArgumentException("フォロー対象が存在しません"));
		followsRepository.deleteByFollowerAndFollowee(follower, followee);
	}
	/**
	 * ログインユーザーが指定ユーザーをフォローしているかどうかを確認する。
	 * 
	 * @param followerId フォロワーのユーザーID
	 * @param followeeId フォローされる側のユーザーID
	 * @return フォローしていればtrue
	 */
	public boolean isFollowing(UUID followerId, UUID followeeId) {
		Users follower = usersRepository.findById(followerId)
				.orElseThrow(() -> new IllegalArgumentException("フォロワーが存在しません"));
		Users followee = usersRepository.findById(followeeId)
				.orElseThrow(() -> new IllegalArgumentException("フォロー対象が存在しません"));
		return followsRepository.existsByFollowerAndFollowee(follower, followee);
	}
}