package com.example.sns.dto;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー検索の候補表示用DTOクラス。
 * 名前・ログインID・ユーザーIDなど、検索結果に必要な最小限の情報を持つ。
 *
 * @author 岡本
 * @since 2025-07-10
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSuggestDto {
    private UUID userId;     // ユーザーID（遷移やリンクに使う場合）
    private String userName; // 表示用ユーザー名
    private String loginId;  // ログインID（@付きで表示に利用）
    private boolean followedByLoginUser; 
    private boolean isSelf;             // 自分自身かどうか（trueなら自分）
    private boolean followingLoginUser;  // 相手が自分をフォローしているか
}