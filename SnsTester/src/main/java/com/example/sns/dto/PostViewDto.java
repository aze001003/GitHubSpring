package com.example.sns.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 投稿一覧表示用のDTOクラス。
 * エンティティの内容を加工し、ビューに渡す。
 * 
 * @author 岡本
 * @since 2025-07-09
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostViewDto {
    private UUID postId;
    private String content;
    private String relativeTime; // ← 相対時間（「5分前」など）
    private String userName;
}