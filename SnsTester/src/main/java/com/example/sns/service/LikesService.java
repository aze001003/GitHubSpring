package com.example.sns.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sns.entity.Likes;
import com.example.sns.entity.Posts;
import com.example.sns.entity.Users;
import com.example.sns.repository.LikesRepository;
import com.example.sns.repository.PostsRepository;
import com.example.sns.repository.UsersRepository;

/**
* いいね機能に関するビジネスロジックを提供するサービスクラス。
* ユーザーのいいね追加・削除やいいね数のカウント処理を行う。
* トランザクション管理を行い、一貫性のあるデータ操作を保証する。
* 
* @author 岡本
* @since 2025-07-10
*/
@Service
public class LikesService {
	private final LikesRepository likesRepository;
	private final UsersRepository usersRepository;
	private final PostsRepository postsRepository;
	
	public LikesService(
			LikesRepository likesRepository,
			UsersRepository usersRepository,
			PostsRepository postsRepository) {
		this.likesRepository = likesRepository;
		this.usersRepository = usersRepository;
		this.postsRepository = postsRepository;
	}
	/**
	 * 指定ユーザーによる投稿へのいいね追加処理。
	 * 既にいいね済みの場合は何も行わず無視する。
	 * 
	 * @param userId いいねするユーザーのID(UUID)
	 * @param postIdStr いいね対象の投稿ID（文字列UUID形式）
	 * @throws IllegalArgumentException ユーザーまたは投稿が存在しない場合にスローされる
	 */
	@Transactional
	public void addLike(UUID userId, String postIdStr) {
		UUID postId = UUID.fromString(postIdStr);
		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません"));
		Posts post = postsRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("投稿が存在しません"));
		// すでにいいね済みか確認（UNIQUE制約を守る）
		boolean alreadyLiked = likesRepository.existsByUserAndPost(user, post);
		if (alreadyLiked) return;
		Likes like = Likes.builder()
				.user(user)
				.post(post)
				.build();
		likesRepository.save(like);
	}
	/**
	 * 指定投稿のいいね数を取得する読み取り専用トランザクション。
	 * 
	 * @param postId 投稿ID(UUID)
	 * @return いいね数（件数）
	 */
	@Transactional(readOnly = true)
	public int countLikes(UUID postId) {
		return likesRepository.countByPost_PostId(postId);
	}
	/**
	 * 指定ユーザーによる投稿へのいいね解除処理。
	 * 
	 * @param userId いいね解除するユーザーのID(UUID)
	 * @param postIdStr いいね解除対象の投稿ID（文字列UUID形式）
	 * @throws IllegalArgumentException ユーザーまたは投稿が存在しない場合にスローされる
	 */
	@Transactional
	public void removeLike(UUID userId, String postIdStr) {
		UUID postId = UUID.fromString(postIdStr);
		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません"));
		Posts post = postsRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("投稿が存在しません"));
		likesRepository.deleteByUserAndPost(user, post);
	}
}