package com.example.sns.util;

import java.util.Optional;
import java.util.UUID;

/**
 * UUID変換・検証ユーティリティクラス。
 * 文字列をUUIDに変換し、無効な場合はOptional.empty()を返す。
 * 
 * @author 岡本
 * @since 2025-07-10
 */
public class UuidValidatorUtil {
	private UuidValidatorUtil() {} // インスタンス化防止
	
	public static Optional<UUID> parseUuid(String uuidStr) {
		try {
			return Optional.of(UUID.fromString(uuidStr));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}