/**
 * ファイル名: follow_api.js
 * 概要:
 * - フォロー/アンフォローのAPI呼び出し共通処理
 * 
 * @author 岡本
 * @since 2025-07-11
 */
import { getCsrfHeaders } from '../util/csrf_util.js';

/**
 * フォロー/フォロー解除APIを呼び出す
 * ※ RESTfulに POST（フォロー） / DELETE（アンフォロー）を使い分ける
 *
 * @param {string} userId - 対象ユーザーID
 * @param {boolean} isFollowing - trueなら現在フォロー済み（→解除） / falseなら未フォロー（→フォロー）
 * @returns {Promise<Response>} - 成功時はレスポンスを返す。失敗時は例外をスロー。
 */
export function toggleFollow(userId, isFollowing) {
  const method = isFollowing ? 'DELETE' : 'POST'; // 状態に応じてHTTPメソッド切り替え
  const url = `/api/follows/${userId}`;
  const headers = getCsrfHeaders(method); // ✅ CSRFトークン取得済み

  return fetch(url, {
    method,
    headers,
    credentials: 'same-origin', // Cookie認証等を維持
  }).then(res => {
    if (!res.ok) throw new Error('フォロー操作に失敗しました');
    return res;
  });
}