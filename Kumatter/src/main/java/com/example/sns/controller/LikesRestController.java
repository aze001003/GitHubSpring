package com.example.sns.controller;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sns.security.UsersDetails;
import com.example.sns.service.LikesService;
import com.example.sns.validator.PostIdValidator;

/**
 * いいね機能のREST APIコントローラークラス。
 * クライアントの非同期リクエストに対応し、
 * いいねの追加・解除をJSON形式で受け取り、結果をJSONで返す。
 * 
 * 認証ユーザー情報は @AuthenticationPrincipal で取得し、
 * バリデーションは PostIdValidator に委譲する。
 * 
 * APIパスは /api/likes 以下にマッピング。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/api/likes")
public class LikesRestController {

    private final LikesService likesService;

    public LikesRestController(LikesService likesService) {
        this.likesService = likesService;
    }

    /**
     * いいね追加のPOST API。
     * 
     * @param loginUserDetails ログイン中のユーザー情報
     * @param request JSON形式でpostIdを含むマップ
     * @return 成功時はliked=true, 現在のいいね数を返す
     */
    @PostMapping("/add")
    public ResponseEntity<?> addLike(
        @AuthenticationPrincipal UsersDetails loginUserDetails,
        @RequestBody Map<String, String> request) {
        return handleLikeRequest(loginUserDetails, request, true);
    }

    /**
     * いいね解除のPOST API。
     * 
     * @param loginUserDetails ログイン中のユーザー情報
     * @param request JSON形式でpostIdを含むマップ
     * @return 成功時はliked=false, 現在のいいね数を返す
     */
    @PostMapping("/remove")
    public ResponseEntity<?> removeLike(
        @AuthenticationPrincipal UsersDetails loginUserDetails,
        @RequestBody Map<String, String> request
    ) {
        return handleLikeRequest(loginUserDetails, request, false);
    }

    /**
     * 共通のいいね追加・解除処理。
     * 
     * @param loginUserDetails ログイン中のユーザー情報
     * @param request リクエストボディ
     * @param isAdd true:追加, false:解除
     * @return HTTPレスポンス
     */
    private ResponseEntity<?> handleLikeRequest(
        UsersDetails loginUserDetails,
        Map<String, String> request,
        boolean isAdd
    ) {
        if (loginUserDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "ログインしてください"));
        }

        Optional<UUID> postIdOpt = PostIdValidator.validatePostId(request);
        if (postIdOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "postIdが不正です"));
        }

        UUID postId = postIdOpt.get();
        try {
            UUID userId = loginUserDetails.getUser().getUserId();
            if (isAdd) {
                likesService.addLike(userId, postId.toString());
            } else {
                likesService.removeLike(userId, postId.toString());
            }

            int likeCount = likesService.countLikes(postId);
            return ResponseEntity.ok(Map.of(
                "liked", isAdd,
                "likeCount", likeCount
            ));
        } catch (Exception e) {
            String msg = isAdd ? "いいね処理に失敗しました" : "いいね解除処理に失敗しました";
            return ResponseEntity.status(500).body(Map.of("error", msg));
        }
    }
}