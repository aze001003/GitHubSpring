package com.example.sns.dto;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * 投稿一覧表示用のDTOクラス。
 * Postsエンティティの情報に加え、表示に必要な加工済みの情報を持つ。
 * 例えば、投稿の相対時間表示やログインユーザーのいいね状態などを含む。
 * ビュー（Thymeleafなど）に渡すことで画面表示を容易にする。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostViewDto {
	/** 投稿ID(UUID形式) */
	private UUID postId;
	/** 投稿内容（テキスト） */
	private String content;
	/** 投稿からの経過時間を表す相対時刻文字列（例：「5分前」） */
	private String relativeTime;
	/** 投稿者のユーザーID（プロフィールページへのリンク用） */
	private UUID userId;
	/** 投稿者のユーザー名 */
	private String userName;
	/** 投稿者のログインID（画面表示時は@を付けて表示） */
	private String loginId;
	/** 投稿のいいね数合計 */
	private int likeCount;
	/** ログインユーザーがこの投稿にいいね済みかどうかのフラグ */
	private boolean likedByLoginUser;
}