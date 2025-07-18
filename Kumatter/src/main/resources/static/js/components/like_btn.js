/**
 * ファイル名: like_btn.js
 * 概要:
 * - 投稿カードはHTMLで描画済みだが、
 * - いいねボタンはJavaScriptで動的生成し、
 * - クリックイベントをセットアップする初期化スクリプト。
 * 
 * @author 岡本
 * @since 2025-07-15
 */
import { setupLikeButton } from '../api/like_api.js'; // ✅ いいね機能のAPI通信・UI更新処理

/**
 * いいねボタンのDOMを生成する
 * @param {Object} post 投稿データオブジェクト
 * @returns {HTMLElement} いいねボタン要素
 */
function createLikeButton(post) {
  const likeButton = document.createElement('button');
  likeButton.type = 'button';
  likeButton.className = 'like-button';
  if (post.likedByLoginUser) likeButton.classList.add('liked'); // ★←既にいいね済みなら装飾
  likeButton.dataset.postId = post.postId;
  likeButton.textContent = post.likedByLoginUser ? '★' : '☆'; // ビジュアル表示
  return likeButton;
}

/**
 * 投稿一覧データに基づき、HTMLの投稿カード内に
 * いいねボタンを動的生成・挿入し、
 * イベントをセットアップする。
 *
 * @param {Array<Object>} posts 投稿データ配列（各投稿に postId 等を含む）
 * @param {HTMLElement} container 投稿カードが含まれる親要素（例: timeline, user profile）
 */
export function initializeLikeButtons(posts, container) {
  posts.forEach(post => {
    // container内でpostId一致の投稿カードを探す
    const postCard = container.querySelector(`.post-card[data-post-id="${post.postId}"]`);
    if (!postCard) return; // 投稿が見つからなければスキップ

    const actions = postCard.querySelector('.post-actions'); // アクション領域
    if (!actions) return;

    // 二重登録防止：すでにあるボタンを削除
    const oldBtn = actions.querySelector('.like-button');
    if (oldBtn) oldBtn.remove();

    // 新しいボタンを生成し追加
    const likeBtn = createLikeButton(post);
    actions.prepend(likeBtn); // 先頭に追加

    // クリックイベント設定（中でAPI呼び出し）
    setupLikeButton(likeBtn);
  });
}

/**
 * ページ直下の例：グローバル変数 window.postsData が存在する場合
 */
document.addEventListener('DOMContentLoaded', () => {
  if (window.postsData && window.postsContainer) {
    initializeLikeButtons(window.postsData, window.postsContainer);
  }
});