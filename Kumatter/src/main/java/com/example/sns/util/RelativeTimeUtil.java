package com.example.sns.util;
import java.time.Duration;
import java.time.LocalDateTime;
/**
* 投稿日時を現在時刻からの相対時間（例：「5分前」「2時間前」）に変換するユーティリティクラス。
*
* Web表示用に、CreatedAtなどの絶対時刻をユーザーに分かりやすい形で表示したい場合に使用。
* 
* @author 岡本
* @since 2025-07-09
*/
public class RelativeTimeUtil {
	/**
	 * 指定された時刻から現在時刻までの時間差を元に、相対的な時間文字列に変換する。
	 *
	 * @param time 対象となる日時（投稿時刻など）
	 * @return 相対時間の文字列（例：「たった今」「5分前」「3時間前」「2日前」など）
	 */
	public static String toRelativeTime(LocalDateTime time) {
		if (time == null) return "";
		// 時間差（Duration）を計算
		Duration duration = Duration.between(time, LocalDateTime.now());
		if (duration.toMinutes() < 1) {
			return "たった今";
		} else if (duration.toMinutes() < 60) {
			return duration.toMinutes() + "分前";
		} else if (duration.toHours() < 24) {
			return duration.toHours() + "時間前";
		} else {
			return duration.toDays() + "日前";
		}
	}
}