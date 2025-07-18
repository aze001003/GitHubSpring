/**
 * ファイル名: home_timeline.js
 * 概要:
 * - ホーム画面のタイムライン表示を担当。
 * - フォロー中・全体モードの切り替えと投稿データの取得・描画を行う。
 * 
 * @author 岡本
 * @since 2025-07-17
 */

import { fetchTimelinePosts } from '../../api/timeline_api.js';
import { renderPostList } from '../../components/timeline_view.js';

/**
 * ホーム画面のタイムラインを描画する。
 *
 * @param {string} mode - タイムライン表示モード ('followed' | 'all')
 *
 * 使用例:
 *   await renderHomeTimeline('followed');
 *
 * 例外処理:
 *   API取得に失敗した場合はコンソールにエラー出力し、ユーザーに通知する。
 */
async function renderHomeTimeline(mode = 'followed') {
  try {
    const posts = await fetchTimelinePosts(mode);
    const container = document.querySelector(`.${mode}-list`);
    if (!container) return;

    renderPostList(container, posts);
  } catch (error) {
    console.error('タイムライン取得エラー:', error);
    alert('タイムラインの取得に失敗しました。');
  }
}

/**
 * タイムラインのタブ切替処理をセットアップする。
 * タブクリック時に投稿リストを切り替え、対応する投稿を再取得・再描画する。
 *
 * 使用例:
 *   setupTabSwitching();
 */
function setupTabSwitching() {
  const tabButtons = document.querySelectorAll('.tab-button');
  const postLists = document.querySelectorAll('.post-list');

  tabButtons.forEach(btn => {
    btn.addEventListener('click', async () => {
      const mode = btn.dataset.mode;
      if (!mode) return;

      tabButtons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      postLists.forEach(list => list.classList.remove('active'));
      const targetList = document.querySelector(`.${mode}-list`);
      if (targetList) targetList.classList.add('active');

      await renderHomeTimeline(mode);
    });
  });
}

document.addEventListener('DOMContentLoaded', async () => {
  await renderHomeTimeline('followed'); // 初期表示はフォロー中投稿
  setupTabSwitching();
});