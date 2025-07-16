package com.example.sns.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sns.dto.UserSuggestDto;
import com.example.sns.security.UsersDetails;
import com.example.sns.service.UsersService;

/**
 * ユーザー名またはログインIDによる予測検索APIコントローラー。
 * 入力文字列に対して部分一致検索を行い、候補を最大10件返す。
 * 
 * SQLインジェクション防止のため、検索語はバインド処理される。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/api/users/suggest")
public class UserSuggestRestController {

    private final UsersService usersService;

    public UserSuggestRestController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * 入力語に部分一致するユーザー候補を返すAPI。
     * 
     * @param query 検索語（2文字以上で有効）
     * @param loginUserDetails 現在認証済みユーザーの情報（Spring Securityから注入）
     * @return 候補ユーザー一覧（最大10件）
     */
    @GetMapping
    public ResponseEntity<?> suggestUsers(
            @RequestParam("query") String query,
            @AuthenticationPrincipal UsersDetails userDetails) {

        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.badRequest().body("検索語は2文字以上入力してください。");
        }

        UUID loginUserId = userDetails != null ? userDetails.getUser().getUserId() : null;

        List<UserSuggestDto> suggestions = usersService.searchUserSuggestions(query.trim(), loginUserId);
        return ResponseEntity.ok(suggestions);
    }
}
