/**
 * ファイル名: timeline_api.js
 * 概要:
 *   タイムライン投稿データの取得APIを呼び出す関数群。
 *   - フォロー中の投稿 or 全体投稿の取得
 *   - 指定ユーザーの投稿の取得
 * 
 * @author 岡本
 * @since 2025-07-17
 */

/**
 * タイムライン（フォロー中 or 全体）投稿を取得するAPI。
 * 
 * @param {string} mode - 'followed'（フォロー中） or 'all'（全体）
 * @returns {Promise<Array<Object>>} 投稿データの配列
 */
export async function fetchTimelinePosts(mode = 'followed') {
  const url = mode === 'all' ? '/api/posts/all' : '/api/posts/timeline';

  const response = await fetch(url); // GETなのでCSRF不要
  if (!response.ok) {
    throw new Error(`タイムラインの取得に失敗しました: ${response.status}`);
  }

  return await response.json(); // 投稿データの配列を返す
}

/**
 * 指定ユーザーの投稿一覧を取得するAPI。
 * 
 * @param {string} userId - 投稿を取得する対象ユーザーのID
 * @returns {Promise<Array<Object>>} 投稿データの配列
 */
export async function fetchUserPosts(userId) {
  if (!userId) {
    throw new Error('ユーザーIDが指定されていません');
  }

  const response = await fetch(`/api/posts/user/${userId}`); // こちらもGETなのでCSRF不要
  if (!response.ok) {
    throw new Error(`ユーザー投稿の取得に失敗しました: ${response.status}`);
  }

  return await response.json(); // 投稿データの配列を返す
}