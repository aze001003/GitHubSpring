/**
 * ファイル名: follow_api.js
 * 作成者: 岡本
 * 作成日: 2025-07-11
 * 概要: フォロー/アンフォローのAPI呼び出し共通処理
 */
import { getCsrfHeaders } from './csrf_util.js';

/**
 * フォロー/フォロー解除APIを呼び出す
 * @param {string} userId - 対象ユーザーID
 * @param {boolean} isFollowing - 現在フォロー中かどうか
 * @returns {Promise<Response>}
 */
export function toggleFollow(userId, isFollowing) {
  const method = isFollowing ? 'DELETE' : 'POST';
  const url = `/api/follows/${userId}`;
  const headers = getCsrfHeaders(method);

  return fetch(url, {
    method,
    headers,
    credentials: 'same-origin',
  }).then(res => {
    if (!res.ok) throw new Error('フォロー操作に失敗しました');
    return res;
  });
}