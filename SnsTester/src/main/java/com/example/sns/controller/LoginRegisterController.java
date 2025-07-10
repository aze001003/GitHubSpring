package com.example.sns.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sns.dto.RegisterRequest;
import com.example.sns.entity.Users;
import com.example.sns.service.AuthenticationService;
import com.example.sns.service.UsersService;
import com.example.sns.util.LoginIdGeneratUtil;



/**
 * ユーザーのログイン、登録、ホーム画面表示、ログアウトを制御するコントローラークラス。
 * セッションおよびSpring Securityの認証情報を使用してログイン状態を維持する。
 *
 * @author 岡本
 * @since 2025-07-07
 */
@Controller
@RequestMapping("/users")
public class LoginRegisterController {
	private final UsersService usersService;
	private final AuthenticationService authenticationService;
	
	public LoginRegisterController(UsersService usersService, AuthenticationService authenticationService) {
		this.usersService = usersService;
		this.authenticationService = authenticationService;
	}
	/**
	 * ログイン画面の表示処理（GET）
	 * - エラーまたはログアウト状態に応じたメッセージを表示。
	 * 
	 * @param error ログイン失敗時に付加されるパラメータ（任意）
	 * @param logout ログアウト時に付加されるパラメータ（任意）
	 * @param model Viewにデータを渡すためのモデル
	 * @param request HTTPリクエストオブジェクト
	 * @return ログイン画面のテンプレート名（"login"）
	 */
	@GetMapping("/login")
	public String loginForm(
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout,
			Model model,
			HttpServletRequest request) {
		if (error  != null) model.addAttribute("errorMessage", "ログインに失敗しました。IDかパスワードを確認してください。");
		if (logout != null) model.addAttribute("logoutMessage", "ログアウトしました。");
		return "login"; // templates/login.htmlを表示
	}
	/**
	 * ユーザー登録画面の表示処理（GET）
	 * @return 登録画面のテンプレート名（"register"）
	 */
	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("registerRequest", new RegisterRequest());
		return "register"; // templates/register.htmlを表示
	}
	/**
	 * ユーザー登録処理（POST）
	 * - フォームから受け取ったパスワードと確認用パスワードの一致をチェックする。
	 * - 一致しない場合はエラーメッセージをセットし、登録画面に戻る。
	 * - 一致した場合は、UsersServiceを使ってユーザー登録を行う。
	 * - 登録成功時は、登録したユーザー情報をセッションに保存し、
	 *   AuthenticationHelperを用いてSpring Securityの認証情報を設定する。
	 * - 登録処理中に例外が発生した場合はエラーメッセージをセットして登録画面に戻る。
	 *
	 * @param email ユーザーのメールアドレス
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @param passwordConfirm 確認用パスワード
	 * @param userBio 任意の自己紹介文
	 * @param session HTTPセッションオブジェクト
	 * @param request HTTPリクエストオブジェクト
	 * @param model Viewへデータを渡すためのモデル
	 * @return 登録成功時はホーム画面へリダイレクト、失敗時は登録画面に戻る
	 */
	@PostMapping("/register")
	public String registerSubmit(
			@Valid @ModelAttribute("registerRequest") RegisterRequest form,
			BindingResult bindingResult,
			@RequestParam String passwordConfirm,
			HttpSession session,
			HttpServletRequest request,
			Model model) {
		if (bindingResult.hasErrors()) {
				return "register";
		}
		if (!form.getPassword().equals(passwordConfirm)) {
			model.addAttribute("passwordConfirmError", "パスワードが一致しません");
			return "register";
}
		try {
			String loginId = LoginIdGeneratUtil.generateLoginIdFromEmail(form.getEmail());
	        Users user = usersService.registerUser(
	                form.getEmail(),
	                loginId,
	                form.getUserName(),
	                form.getPassword(),
	                form.getUserBio());
			authenticationService.loginUser(user, session, request);
			return "redirect:/users/home";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "登録に失敗しました: " + e.getMessage());
			return "register";
		}
	}
	/**
	 * ログアウト処理（GET）
	 * - セッションを無効化とSpring Securityの認証情報をクリア
	 * -ログイン画面にリダイレクトする。
	 * @param session 現在のHTTPセッション（破棄対象）
	 * @return ログアウト完了後にログイン画面へリダイレクト
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		authenticationService.logoutUser(session);
		return "redirect:/users/login?logout";
	}
}