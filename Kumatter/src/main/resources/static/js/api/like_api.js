/**
 * ファイル名: like_api.js
 * 概要: 
 * - 投稿に対する「いいね」「いいね解除」処理を非同期で行う。
 * - CSRF対応やボタン状態の管理、UI更新を含む。
 * 
 * @author 岡本
 * @since 2025-07-17
 */

import { getCsrfHeaders } from '../util/csrf_util.js';

// APIエンドポイント定数
const API_LIKE_ADD = '/api/likes/add';
const API_LIKE_REMOVE = '/api/likes/remove';

/**
 * 指定のいいねボタンにイベントをバインドし、API通信・UI更新を行う。
 *
 * @param {HTMLElement} button - 対象の「いいねボタン」要素
 */
export function setupLikeButton(button) {
  button.addEventListener('click', async () => {
    const postId = button.getAttribute('data-post-id');

    // 現在の状態（クラスによる判定）
    const isLiked = button.classList.contains('liked');
    const url = isLiked ? API_LIKE_REMOVE : API_LIKE_ADD;

    // 二重クリック防止
    button.disabled = true;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: getCsrfHeaders('POST'),
        body: JSON.stringify({ postId }),
        credentials: 'same-origin'
      });

      if (!response.ok) {
        throw new Error('通信エラー');
      }

      const data = await response.json();

      // 表示内容とスタイルの更新
      button.textContent = data.liked ? '★' : '☆';
      button.classList.toggle('liked', data.liked);

      // いいね数表示も更新
      const likeCountSpan = button.parentElement.querySelector('.like-count');
      if (likeCountSpan) {
        likeCountSpan.textContent = data.likeCount;
      }

    } catch (error) {
      console.error(error);
      alert('いいね処理に失敗しました');
    } finally {
      button.disabled = false; // 再度有効化
    }
  });
}
