package com.example.sns.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * * ユーザーのプロフィール画面表示用DTOクラス。
 * Usersエンティティの情報に加え、画面で必要な派生情報（投稿数やフォロー数など）を含む。
 * エンティティを直接Viewに渡すのではなく、このDTOを使うことで
 * 表示に必要な情報だけを安全かつ効率的に取り出せる。
 * 
 * @author 岡本
 * @since 2025-07-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
	/** ユーザーID（UUID形式）: プロフィールリンク等で使用 */
	private UUID userId;
	/** 表示用ユーザー名 */
	private String userName;
	/** ユーザーの自己紹介文（任意入力） */
	private String userBio;
	/** 投稿数: このユーザーが投稿した総数（削除済みは含まない） */
	private int postCount;
	/** フォロー中数: このユーザーがフォローしている人数 */
	private int followingCount;
	/** フォロワー数: このユーザーをフォローしている人数 */
	private int followerCount;
	/** いいね数: このユーザーが「いいね」した投稿の数 */
	private int likedPostCount;
}
