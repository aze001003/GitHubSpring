package com.example.sns.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;

import lombok.Getter;
import lombok.Setter;
/**
* ユーザー情報を表すJPAエンティティクラス。
* DBの users テーブルに対応します。
* 
* @author 岡本
* @since 2025-07-07
*/
@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {
	/**
	 * ユーザーID(UUID)。
	 * Hibernate 6.2以降推奨のUUID生成アノテーションを使用し、
	 * DBのUUID型カラムにマッピング。
	 */
	@Id
	@GeneratedValue
	@UuidGenerator // Hibernate 6.2以降推奨のUUID生成アノテーション
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID userId;
	/**
	 * メールアドレス。
	 * 最大50文字、null不可、一意制約あり。
	 */
	@Column(name = "email", length = 50, nullable = false, unique = true)
	private String email;
	/**
	 * パスワード（ハッシュ化済み想定）。
	 * 最大100文字、null不可。
	 */
	@Column(name = "password", length = 100, nullable = false)
	private String password;
	/**
	 * ログインID。
	 * 最大50文字、null不可、一意制約あり。
	 */
	@Column(name = "login_id", length = 50, nullable = false, unique = true)
	private String loginId;
	/**
	 * ユーザー名。
	 * 最大20文字、null不可。
	 */
	@Column(name = "user_name", length = 20, nullable = false)
	private String userName;
	/**
	 * 自己紹介文。
	 * 文字数制限なし（TEXT型）。
	 */
	@Column(name = "user_bio", columnDefinition = "TEXT")
	private String userBio;
	/**
	 * レコード作成日時。
	 * null不可、更新時は変更しない。
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	/**
	 * レコード更新日時。
	 * null不可。
	 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	/**
	 * エンティティが永続化される直前に呼び出される。
	 * createdAt, updatedAtを現在日時で初期化。
	 */
	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	/**
	 * エンティティ更新直前に呼び出される。
	 * updatedAtを現在日時に更新。
	 */
	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
