package com.example.sns.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.example.sns.converter.PostTypeConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
* 投稿情報を保持するエンティティクラス。
* 通常投稿・拡散投稿（spread）を区別するためのENUM型と、拡散元を参照するフィールドを持つ。
*
* @author 岡本
* @since 2025-07-09
*/
@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Posts {
	/**
	 * 投稿ID（UUID）。
	 * 主キー制約および一意制約あり。
	 * 自動生成されるUUIDを使用。
	 */
	@Id
	@GeneratedValue
	@Column(name = "post_id", nullable = false, unique = true)
	private UUID postId;
	/**
	 * 投稿内容（本文）。
	 * 通常投稿（post_type='normal'）では必須、
	 * 拡散投稿（post_type='spread'）ではNULL許可。
	 * DB側のCHECK制約で制御。
	 */
	@Column(name = "content")
	private String content;
	/**
	 * 投稿種別。
	 * ENUM（normal / spread）で表現され、
	 * DBでは文字列（VARCHAR）として保存される。
	 */
	@Convert(converter = PostTypeConverter.class)
	@Column(name = "post_type", nullable = false)
	private PostType postType;
	/**
	 * 拡散元の投稿ID。
	 * 拡散投稿（spread）の場合のみ使用される。
	 * 通常投稿ではNULL。
	 */
	@Column(name = "spread_source_id")
	private UUID spreadSourceId;
	/**
	 * 投稿作成日時。
	 * レコード作成時に自動設定。
	 * 更新時は変更されない（updatable = false）。
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	/**
	 * 投稿更新日時。
	 * レコード更新時に自動更新される。
	 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	/**
	 * 投稿を行ったユーザー。
	 * Usersエンティティとの多対一（ManyToOne）リレーション。
	 * 外部キー user_id により紐付けられる。
	 * LAZYフェッチ戦略を使用し、必要時にユーザー情報を取得。
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	/**
	 * エンティティが初めて永続化される直前に呼び出されるコールバック。
	 * 作成日時・更新日時を現在時刻で初期化する。
	 */
	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}
	/**
	 * エンティティが更新される直前に呼び出されるコールバック。
	 * 更新日時を現在時刻に更新する。
	 */
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}