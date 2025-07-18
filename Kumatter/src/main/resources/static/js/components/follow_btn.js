/**
 * ファイル名: follow_btn.js
 * 概要:
 * - フォローボタンのDOM生成とフォローAPI呼び出しの共通部品
 * 
 * @author 岡本
 * @since 2025-07-11
 */
import { toggleFollow } from '../api/follow_api.js';

/**
 * フォローボタンを作成する
 * @param {Object} user - ユーザー情報オブジェクト
 * @param {string} user.userId - ユーザーID
 * @param {boolean} user.followedByLoginUser - ログインユーザーがフォロー済みか
 * @returns {HTMLButtonElement}
 */
export function createFollowButton(user) {
  const btn = document.createElement('button');
  btn.className = 'follow_btn';
  btn.dataset.userId = user.userId;
  btn.textContent = user.followedByLoginUser ? 'フォロー解除' : 'フォロー';

  btn.addEventListener('click', () => {
    const isFollowing = btn.textContent === 'フォロー解除';

    toggleFollow(user.userId, isFollowing)
      .then(() => {
        btn.textContent = isFollowing ? 'フォロー' : 'フォロー解除';
      })
      .catch(err => {
        alert(err.message || '通信エラーが発生しました');
      });
  });

  return btn;
}