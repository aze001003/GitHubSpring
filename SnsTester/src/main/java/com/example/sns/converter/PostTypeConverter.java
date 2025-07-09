package com.example.sns.converter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.example.sns.entity.PostType;
/**
* PostType列挙型とDBのVARCHAR型の相互変換を行うコンバータークラス。
* JavaのEnum（大文字）とDBの文字列（小文字）をマッピングし、
* 永続化時と読み込み時に自動変換される。
* 
* @author 岡本
* @since 2025-07-09
*/
@Converter(autoApply = true)
public class PostTypeConverter implements AttributeConverter<PostType, String> {
	/**
	 * JavaのPostType EnumをDB保存用の文字列に変換する。
	 * nullの場合はnullを返す。
	 * 
	 * @param attribute JavaのPostType Enum
	 * @return DBに保存する小文字文字列（"normal" または "spread"）
	 */
	@Override
	public String convertToDatabaseColumn(PostType attribute) {
		if (attribute == null) return null;
		// Javaの大文字Enumを小文字に変換してDBに保存
		return attribute.name().toLowerCase();
	}
	/**
	 * DBから読み込んだ小文字文字列をJavaのPostType Enumに変換する。
	 * nullの場合はnullを返す。
	 * 
	 * @param dbData DBの小文字文字列
	 * @return JavaのPostType Enum
	 */
	@Override
	public PostType convertToEntityAttribute(String dbData) {
		if (dbData == null) return null;
		// DBの小文字文字列を大文字Enum名に変換してEnumを返す
		return PostType.valueOf(dbData.toUpperCase());
	}
}
