/**
 * ファイル名: user_timeline.js
 * 概要:
 * - ユーザー詳細ページに表示される投稿一覧（タイムライン）の描画を担当。
 * - 対象ユーザーIDはHTMLの <body data-user-id="..."> 属性から取得。
 *
 * 必要な前提:
 * - <body data-user-id="..."> がHTML上に存在していること
 * - 投稿表示用コンテナとして <div id="user-post-list"> が存在していること
 *
 * @author 岡本
 * @since 2025-07-18
 */

import { fetchUserPosts } from '../api/timeline_api.js';
import { renderPostList } from '../components/timeline_view.js';

document.addEventListener('DOMContentLoaded', async () => {
  try {
    // ユーザーIDを<body>のdata属性から取得
    const userId = document.body.dataset.userId;
    if (!userId) {
      console.warn('ユーザーIDが見つかりません（data-user-id が未設定）');
      return;
    }

    // ユーザーの投稿を非同期で取得
    const posts = await fetchUserPosts(userId);

    // 投稿描画先のコンテナを取得
    const container = document.getElementById('user-post-list');
    if (!container) {
      console.warn('投稿表示コンテナ（id="user-post-list"）が見つかりません');
      return;
    }

    // 投稿一覧を描画
    renderPostList(container, posts);
  } catch (error) {
    console.error('ユーザー投稿の読み込み失敗:', error);
  }
});