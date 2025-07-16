package com.example.sns.util;

/**
* メールアドレスからログインIDを生成するユーティリティクラス。
* - メールアドレスの「@」より前の部分を抽出し、小文字化する。
* - ログインIDとして許容する文字（英数字、小文字のアンダースコア・ハイフン）のみを残す。
* - 無効な文字は削除し、ログインIDとして適切な形式に変換する。
* 注意：
* - ログインIDの重複チェックやユニーク性の保証はこのクラスの責務ではなく、
*   サービス層などで行うことを想定している。
* - 不正なメール形式や変換後に有効な文字が存在しない場合は例外を投げる。
* 
* @author 岡本
* @since 2025-07-09
*/
public class LoginIdGeneratUtil {
	/**
	 * メールアドレスからログインIDを生成する。
	 * 
	 * @param email メールアドレス（nullや不正な形式は例外を投げる）
	 * @return ログインID（小文字かつ許容文字のみで構成）
	 * @throws IllegalArgumentException メール形式が不正、または有効な文字がない場合
	 */
	public static String generateLoginIdFromEmail(String email) {
		if (email == null || !email.contains("@")) {
			throw new IllegalArgumentException("不正なメールアドレス形式です: " + email);
		}
		String base = email.split("@")[0].toLowerCase();
		// 許容される文字だけ抽出（英数字、アンダースコア、ハイフン）
		String cleaned = base.replaceAll("[^a-z0-9_-]", "");
		if (cleaned.isEmpty()) {
			throw new IllegalArgumentException("メールアドレスの@より前の部分に有効な文字がありません: " + email);
		}
		return cleaned;
	}
}
