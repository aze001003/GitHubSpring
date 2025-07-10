package com.example.sns.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Likesエンティティ：投稿に対する「いいね」を表現するクラス。
 * 各いいねはユーザーと投稿の組み合わせで一意。
 * 複合ユニーク制約により、同じユーザーが同じ投稿に複数回いいねすることを防止。
 * 
 * リレーション：
 * - Usersエンティティに対して多対一（ManyToOne）
 * - Postsエンティティに対して多対一（ManyToOne）
 *
 * @author 岡本
 * @since 2025-07-10
 */
@Entity
@Table(name = "likes",
	uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Likes {
	/**
	 * いいねID（主キー）。自動生成されるUUID。
	 */
	@Id
	@GeneratedValue
	@Column(name = "like_id", nullable = false)
	private UUID likeId;
	/**
	 * いいねしたユーザー。
	 * Usersエンティティと多対一のリレーション。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	/**
	 * いいね対象の投稿。
	 * Postsエンティティと多対一のリレーション。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Posts post;
	/**
	 * いいねした日時。
	 * エンティティ永続化時に自動で現在時刻を設定。
	 */
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	/**
	 * エンティティ初回保存時に呼ばれ、作成日時を自動設定する。
	 */
	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}