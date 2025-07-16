package com.example.sns.validator;
import org.springframework.stereotype.Component;

import com.example.sns.dto.RegisterRequest;
/**
* ユーザー登録フォームの入力値バリデーションを担当するクラス。
* 特にパスワードと確認用パスワードの一致チェックを集約し、Controllerの責務を軽減します。
* その他の項目のバリデーションはDTOのアノテーションで行います。
* 
* @author 岡本
* @since 2025-07-09
*/
@Component
public class UserRegistrationValidator {
	/**
	 * ユーザー登録フォームのパスワード一致チェックを行う。
	 * 
	 * @param request 登録フォームのDTO
	 * @return エラーメッセージ。問題なければnullまたは空文字。
	 */
	public String validate(RegisterRequest request) {
		if (request.getPassword() == null || request.getPasswordConfirm() == null) {
			return "パスワードは必須です。";
		}
		if (!request.getPassword().equals(request.getPasswordConfirm())) {
			return "パスワードが一致しません。";
		}
		return null;
	}
}