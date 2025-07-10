package com.example.sns.validator;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.example.sns.util.UuidValidatorUtil;

/**
 * 投稿IDに関するバリデーションを行うクラス。
 * リクエストからpostIdを取得し、UUID形式かどうかを検証する。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
public class PostIdValidator {

    private PostIdValidator() {} // インスタンス化防止

    /**
     * postIdが存在し、UUID形式として妥当ならOptionalに包んで返す。
     * 
     * @param requestBody JSON形式のMapリクエスト
     * @return UUIDが妥当な場合はそのUUID、そうでなければempty
     */
    public static Optional<UUID> validatePostId(Map<String, String> requestBody) {
        String postIdStr = requestBody.get("postId");
        return postIdStr == null ? Optional.empty() : UuidValidatorUtil.parseUuid(postIdStr);
    }
}