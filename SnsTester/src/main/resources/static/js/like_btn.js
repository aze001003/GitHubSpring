/**
 * ファイル名: like_btn.js
 * 作成者: 岡本
 * 作成日: 2025-07-15
 * 概要:
 *   投稿カードはHTMLで描画済みだが、
 *   いいねボタンはJavaScriptで動的生成し、
 *   クリックイベントをセットアップする初期化スクリプト。
 */

import { setupLikeButton } from './like_api.js';

/**
 * いいねボタンのDOMを生成する
 * @param {Object} post 投稿データオブジェクト
 * @returns {HTMLElement} いいねボタン要素
 */
function createLikeButton(post) {
  const likeButton = document.createElement('button');
  likeButton.type = 'button';
  likeButton.className = 'like-button';
  if (post.likedByLoginUser) likeButton.classList.add('liked');
  likeButton.dataset.postId = post.postId;
  likeButton.textContent = post.likedByLoginUser ? '★' : '☆';
  return likeButton;
}

/**
 * 投稿一覧データに基づき、HTMLの投稿カード内に
 * いいねボタンを動的生成・挿入し、
 * イベントをセットアップする。
 * @param {Array<Object>} posts 投稿データ配列
 */
export function initializeLikeButtons(posts) {
  posts.forEach(post => {
    // 投稿カードのDOMをdata-post-id属性から取得
    const postCard = document.querySelector(`.post-card[data-post-id="${post.postId}"]`);
    if (!postCard) return;

    const actions = postCard.querySelector('.post-actions');
    if (!actions) return;

    // 既存のいいねボタンがあれば削除（念のため）
    const oldBtn = actions.querySelector('.like-button');
    if (oldBtn) oldBtn.remove();

    // いいねボタンを作成して差し込む
    const likeBtn = createLikeButton(post);
    actions.prepend(likeBtn);

    // クリックイベントを設定
    setupLikeButton(likeBtn);
  });
}

// ページ読み込み後の例（postsデータの取得方法に応じて実装を変えてください）
document.addEventListener('DOMContentLoaded', () => {
  // 例：グローバル変数などでpostsデータがある場合
  initializeLikeButtons(window.postsData);
});
