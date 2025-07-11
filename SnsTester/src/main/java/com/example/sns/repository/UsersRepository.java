package com.example.sns.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sns.entity.Users;
/**
* Usersエンティティのデータアクセスを行うリポジトリインターフェース。
* JpaRepositoryを継承することで、基本的なCRUD操作やページング・ソート機能を提供。
* 
* @author 岡本
* @since 2025-07-07
*/
public interface UsersRepository extends JpaRepository<Users, UUID> {
	/**
	 * メールアドレスで存在確認を行う。
	 * @param email メールアドレス
	 * @return 存在すればtrue、それ以外はfalse
	 */
	boolean existsByEmail(String email);
	/**
	 * ログインIDで存在確認を行う。
	 * @param loginId ログインID
	 * @return 存在すればtrue、それ以外はfalse
	 */
	boolean existsByLoginId(String loginId);
	/**
	 * ログインIDまたはメールアドレスでユーザー検索する。
	 * @param loginId ログインID
	 * @param email メールアドレス
	 * @return 見つかればUsersをOptionalで返す。存在しなければ空のOptional。
	 */
	Optional<Users> findByLoginIdOrEmail(String loginId, String email);
	/**
	 * 入力されたキーワードに部分一致するユーザー名またはログインIDを持つユーザー一覧を返す。
	 * ※ SQLインジェクション対策のため、バインド変数を使用
	 *
	 * @param keyword 入力文字列（2文字以上を想定）
	 * @return 部分一致するユーザー一覧（最大20件などに制限するのも可）
	 */
	@Query("SELECT u FROM Users u WHERE u.userName LIKE %:keyword% OR u.loginId LIKE %:keyword%")
	List<Users> searchByKeyword(@Param("keyword") String keyword);
	
	List<Users> findTop10ByLoginIdContainingIgnoreCaseOrUserNameContainingIgnoreCase(String loginId, String userName);
}