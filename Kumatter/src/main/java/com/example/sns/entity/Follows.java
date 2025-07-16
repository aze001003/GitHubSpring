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
 * Followsエンティティ：ユーザー間のフォロー関係を表現するクラス。
 * 
 * follower（フォロワー）と followee（フォロー先）の組み合わせが一意である必要があるため、
 * follower_id と followee_id に複合ユニーク制約を設けている。
 * 
 * リレーション：
 * - follower: フォローするユーザー（Usersエンティティへの多対一）
 * - followee: フォローされるユーザー（Usersエンティティへの多対一）
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@Entity
@Table(name = "follows",
	uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follows {
	/**
	 * フォローID（主キー）。自動生成されるUUID。
	 */
	@Id
	@GeneratedValue
	@Column(name = "follow_id", nullable = false)
	private UUID followId;
	/**
	* フォロワー（フォローする側のユーザー）。
	* Usersエンティティと多対一のリレーション。
	*/
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id", nullable = false)
	private Users follower;
	/**
	* フォロー先（フォローされる側のユーザー）。
	* Usersエンティティと多対一のリレーション。
	*/
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "followee_id", nullable = false)
	private Users followee;
	/**
	 * フォロー日時。
	 * 初回保存時（@PrePersist）に現在時刻が自動設定される。
	 */
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	/**
	 * エンティティの初回永続化時に、作成日時を現在時刻に設定する。
	 */
	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}