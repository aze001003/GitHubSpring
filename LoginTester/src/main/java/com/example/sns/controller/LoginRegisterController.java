package com.example.sns.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sns.entity.Users;
import com.example.sns.security.UsersDetails;
import com.example.sns.service.UsersService;

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
	
	public LoginRegisterController(UsersService usersService) {
		this.usersService = usersService;
	}
	/**
	 * ログイン画面の表示処理（GET）
	 * - エラーまたはログアウト状態に応じたメッセージを表示。
	 */
	@GetMapping("/login")
	public String loginForm(
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout,
			Model model,
			HttpServletRequest request) {
		if (error != null) {
			model.addAttribute("errorMessage", "ログインに失敗しました。IDかパスワードを確認してください。");
		}
		if (logout != null) {
			model.addAttribute("logoutMessage", "ログアウトしました。");
		}
		return "login"; // templates/login.htmlを表示
	}
	/**
	 * ユーザー登録画面の表示処理（GET）
	 */
	@GetMapping("/register")
	public String registerForm() {
		return "register"; // templates/register.htmlを表示
	}
	/**
	 * ユーザー登録処理（POST）
	 * - パスワード確認欄と一致するかをチェック。
	 * - メールアドレスからログインIDを自動生成。
	 * - 登録成功時はセッションおよびSpring Securityへ認証情報を設定。
	 * - 登録失敗時はエラーメッセージ付きで戻る。
	 */
	@PostMapping("/register")
	public String registerSubmit(
			@RequestParam String email,
			@RequestParam String userName,
			@RequestParam String password,
			@RequestParam String passwordConfirm,
			@RequestParam(required = false) String userBio,
			HttpSession session,
			HttpServletRequest request,
			Model model) {
		// パスワード一致確認
		if (!password.equals(passwordConfirm)) {
			model.addAttribute("errorMessage", "パスワードが一致しません");
			model.addAttribute("email", email);
			model.addAttribute("userName", userName);
			return "register";
		}
		try {
			String loginId = email.split("@")[0];// ログインIDはメールアドレスの@より前
			Users user = usersService.registerUser(email, loginId, userName, password, userBio);
			session.setAttribute("loginUser", user);// セッションへユーザー情報を保存
			// Spring Security 認証情報の構築
			UsersDetails userDetails = new UsersDetails(user, loginId);
			UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authToken);
			// 認証情報をSpring Securityにセット
			SecurityContextHolder.setContext(context);
			// 認証状態をセッションにも保存（自動ログイン状態を保持）
			request.getSession(true).setAttribute(
					HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
					context);
			return "redirect:/users/home";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "登録に失敗しました: " + e.getMessage());
			model.addAttribute("email", email);
			model.addAttribute("userName", userName);
			return "register";
		}
	}
	/**
	 * ホーム画面の表示処理（GET）
	 * - 認証済みユーザーであればユーザー情報を表示。
	 * - 未認証ならログイン画面へリダイレクト。
	 */
	@GetMapping("/home")
	public String home(HttpSession session, Model model) {
		// 認証済みユーザーをSpring Securityから取得
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UsersDetails) {
			UsersDetails userDetails = (UsersDetails) principal;
			Users loginUser = userDetails.getUser();
			model.addAttribute("user", loginUser);
			return "home";  // templates/home.htmlを表示
		} else {
			return "redirect:/users/login";
		}
	}
	/**
	 * ログアウト処理（GET）
	 * - セッションを無効化してログイン画面へ遷移。
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/users/login?logout";
	}
}