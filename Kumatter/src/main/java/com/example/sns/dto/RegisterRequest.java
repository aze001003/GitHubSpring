package com.example.sns.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
/**
 * ユーザー登録APIのリクエストパラメータを表すDTOクラス。
 * バリデーションアノテーションにより入力チェックを行います。
 * 各フィールドには入力必須や文字数制限、形式チェックなどを設定。
 * 
 * @author 岡本
 * @since 2025-07-07
 */
@Getter
@Setter
public class RegisterRequest {
	/**
	 * メールアドレス
	 * -必須入力
	 * -正しいメール形式
	 * -最大50文字まで（DBのVARCHAR(50)に合わせる）
	 */
	@Email(message    = "メールアドレスの形式が正しくありません")
	@NotBlank(message = "メールアドレスは必須です")
	@Size(message     = "メールアドレスは50文字以内で入力してください", max = 50)
	private String email;
	/**
	 * ユーザー名
	 * -必須入力
	 * -最大20文字まで（DBのVARCHAR(20)に合わせる）
	 */
	@NotBlank(message = "ユーザー名は必須です")
	@Size(message     = "ユーザー名は20文字以内で入力してください", max = 20)
	private String userName;
	/**
	 * パスワード
	 * -必須入力
	 * -6文字以上（セキュリティ上の最低文字数）
	 * -最大100文字まで（DBのVARCHAR(100)に合わせる）
	 */
	@NotBlank(message = "パスワードは必須です")
	@Size(message     = "パスワードは6文字以上100文字以内で入力してください", min = 6, max = 100)
	private String password;
	/**
	 * 確認用パスワード（フォーム入力のみでDTOに保持）
	 * バリデーションアノテーションは不要で、
	 * コントローラーやバリデータでのチェック対象とする
	 */
	private String passwordConfirm;
	/**
	 * 自己紹介
	 * -任意入力
	 * -文字数制限なし（DBのTEXTに合わせる）
	 */
	private String userBio;
}