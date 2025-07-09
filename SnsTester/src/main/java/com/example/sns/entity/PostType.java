package com.example.sns.entity;

/**
 * 投稿の種別を表すENUM。
 * - NORMAL: 通常の投稿
 * - SPREAD: 拡散投稿（リツイートのようなもの）
 * 
 * 現時点ではすべて NORMAL 投稿のみ使用。
 * 将来的に SPREAD（拡散）機能を追加する際に利用予定。
 *
 * @author 岡本
 * @since 2025-07-09
 */
public enum PostType {
	NORMAL,
	SPREAD
}