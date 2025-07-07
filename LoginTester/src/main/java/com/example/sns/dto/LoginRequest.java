package com.example.sns.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * ログインAPIのリクエストパラメータを表すDTOクラス。
 * フィールドにはバリデーションアノテーションを付与し、
 * 入力必須チェックを行います。
 * 
 * @author 岡本
 * @since 2025-07-07
 */
@Getter
@Setter
public class LoginRequest {
	/** ログインIDまたはメールアドレス。空白不可 */
	@NotBlank(message = "ログインIDまたはメールアドレスは必須です")
	private String loginIdOrEmail;
	/** パスワード。空白不可 */
	@NotBlank(message = "パスワードは必須です")
	private String password;
}