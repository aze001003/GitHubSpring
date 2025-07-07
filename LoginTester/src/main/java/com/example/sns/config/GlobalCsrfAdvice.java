package com.example.sns.config;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
/**
 * 全てのテンプレートにCSRFトークンを自動で渡す設定クラス。
 * フォームで以下のように使用可能：
 * <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
 * 各コントローラーで明示的に設定しなくても済むようにする。
 * 
 * @author 岡本
 * @since 2025-07-07
 */
@ControllerAdvice
public class GlobalCsrfAdvice {
	/**
	 * リクエストからCSRFトークンを取得し、Modelに追加する。
	 * 
	 * @param model   Viewに渡すModel
	 * @param request 現在のHTTPリクエスト
	 */
	@ModelAttribute
	public void addCsrfToken(Model model, HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (token != null) {
			model.addAttribute("_csrf", token);
		}
	}
}