package com.example.sns.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.sns.dto.PostViewDto;
import com.example.sns.entity.Users;
import com.example.sns.security.UsersDetails;
import com.example.sns.service.PostsService;
import com.example.sns.service.UsersService;

/**
 * 投稿に関するREST APIを提供するコントローラークラス。
 * 主にタイムラインや特定ユーザーの投稿一覧の取得を扱う。
 * APIは認証済みユーザーのみアクセス可能とし、
 * 非ログインユーザーからのアクセスは拒否する。
 * 
 * @author 岡本
 * @since 2025-07-17
 */
@RestController
@RequestMapping("/api/posts")
public class PostsRestController {

    private final PostsService postsService;
    private final UsersService usersService;

    public PostsRestController(PostsService postsService, UsersService usersService) {
        this.postsService = postsService;
        this.usersService = usersService;
    }

    /**
     * ログインユーザーのタイムライン（フォロー中ユーザー＋自分の投稿）を取得する。
     * ログインしていなければ401 Unauthorizedを返す。
     * 
     * @param loginUserDetails 認証済みユーザー情報
     * @return 投稿DTOリスト（作成日時降順）
     */
    @GetMapping("/timeline")
    public List<PostViewDto> getTimeline(@AuthenticationPrincipal UsersDetails loginUserDetails) {
        if (loginUserDetails == null) {
            // 非ログインユーザーは拒否
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です");
        }
        return postsService.getFollowedUsersPostsWithLikes(loginUserDetails.getUser());
    }

    /**
     * 指定されたユーザーIDの投稿一覧を取得するAPI。
     * ログイン済みユーザーのみアクセス可能。
     * 指定ユーザーが存在しなければ404 Not Foundを返す。
     * 
     * @param userId 取得対象のユーザーID(UUID)
     * @param loginUserDetails 認証済みユーザー情報
     * @return 投稿DTOリスト（作成日時降順）
     */
    @GetMapping("/user/{userId}")
    public List<PostViewDto> getPostsByUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UsersDetails loginUserDetails) {

        if (loginUserDetails == null) {
            // 非ログインユーザーは拒否
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です");
        }

        Users loginUser = loginUserDetails.getUser();

        // ユーザー存在チェック
        Users user = usersService.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定ユーザーは存在しません");
        }

        // 投稿一覧を取得（ログインユーザー判定でいいね状態も含む）
        return postsService.getPostsByUserWithLikes(loginUser, user);
    }
    
    @GetMapping("/all")
    public List<PostViewDto> getAllPosts(@AuthenticationPrincipal UsersDetails loginUserDetails) {
        if (loginUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です");
        }
        // postsServiceに全投稿取得メソッドがあることが前提
        return postsService.getAllPostsWithLikes(loginUserDetails.getUser());
    }
}