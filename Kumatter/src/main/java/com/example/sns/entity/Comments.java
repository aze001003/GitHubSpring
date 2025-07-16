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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Commentsエンティティ：投稿に対するコメント情報を表現するクラス。
 *
 * 各コメントは、必ず特定の投稿および投稿者（ユーザー）に紐付いている。
 * コメントの本文（content）はNULL不可。作成日時・更新日時も保持。
 *
 * リレーション：
 * - post：どの投稿に対するコメントか（Postsエンティティへの多対一）
 * - user：誰がコメントしたか（Usersエンティティへの多対一）
 *
 * @author 岡本
 * @since 2025-07-10
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comments {
	/**
	 * コメントID（主キー）。UUID形式で自動生成。
	 */
	@Id
	@GeneratedValue
	@Column(name = "comment_id", nullable = false)
	private UUID commentId;
	/**
	 * コメント対象の投稿。
	 * Postsエンティティへの多対一のリレーション。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Posts post;
	/**
	 * コメント投稿者（ユーザー）。
	 * Usersエンティティへの多対一のリレーション。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	/**
	 * コメントの本文。
	 * NULL不可。長文にも対応できるようにTEXT型で定義。
	 */
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;
	/**
	 * コメントの作成日時。
	 * エンティティ永続化時（@PrePersist）に自動設定。
	 */
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	/**
	 * コメントの最終更新日時。
	 * 作成時と更新時に自動更新される。
	 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	/**
	 * エンティティ初回保存時に呼ばれ、作成日時・更新日時を現在時刻で初期化する。
	 */
	@PrePersist
	public void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}
	/**
	 * エンティティ更新時に呼ばれ、更新日時を現在時刻に更新する。
	 */
	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}