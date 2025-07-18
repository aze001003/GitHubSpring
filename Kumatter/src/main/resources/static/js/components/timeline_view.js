/**
 * ファイル名: timeline_view.js
 * 概要:
 *  タイムライン画面やユーザー投稿画面で共通使用される「投稿カードの描画ロジック」。
 *   - 投稿データから投稿カードDOMを生成する
 *   - 投稿リスト全体を描画する
 *   - 「いいねボタン」初期化もここで行う（UIの責務）
 * 
 * @author 岡本
 * @since 2025-07-17
 */

import { initializeLikeButtons } from './like_btn.js';

/**
 * 投稿カードのHTML要素を生成（PostViewDtoに準拠）
 *
 * @param {Object} post - 投稿データオブジェクト
 * @param {string} post.postId
 * @param {string} post.userId
 * @param {string} post.userName
 * @param {string} post.loginId
 * @param {string} post.content
 * @param {string} post.relativeTime - 表示用の経過時間（例: "3分前"）
 * @param {number} post.likeCount
 * @returns {HTMLElement} - 投稿カードDOM要素
 */
export function createPostCard(post) {
  const card = document.createElement('div');
  card.className = 'post-card';
  card.dataset.postId = post.postId;

  // ヘッダー部分
  const header = document.createElement('div');
  header.className = 'post-header';

  const userLink = document.createElement('a');
  userLink.href = `/users/${post.userId}`;
  userLink.textContent = post.userName;
  userLink.className = 'post-user-name';

  const loginId = document.createElement('span');
  loginId.className = 'login-id';
  loginId.textContent = `@${post.loginId}`;

  const time = document.createElement('span');
  time.className = 'relative-time';
  time.textContent = post.relativeTime;

  header.append(userLink, loginId, time);

  // コンテンツ部分
  const content = document.createElement('div');
  content.className = 'post-content';
  content.textContent = post.content;

  // アクション部分（いいねなど）
  const actions = document.createElement('div');
  actions.className = 'post-actions';

  const likeCount = document.createElement('span');
  likeCount.className = 'like-count';
  likeCount.textContent = post.likeCount;

  actions.appendChild(likeCount);

  // 投稿カードに各パーツを追加
  card.append(header, content, actions);

  return card;
}

/**
 * 投稿リストを描画（タイムライン・ユーザー投稿の共通UI処理）
 *
 * @param {HTMLElement} container - 描画先のDOM要素（例: #timeline-container）
 * @param {Array<Object>} posts - 投稿データ配列
 */
export function renderPostList(container, posts) {
  if (!container) return;

  container.innerHTML = ''; // 一度中身をクリア

  posts.forEach(post => {
    const postCard = createPostCard(post);
    container.appendChild(postCard);
  });

  // 各投稿に「いいねボタン」を初期化（UIイベント登録も）
  initializeLikeButtons(posts, container);
}