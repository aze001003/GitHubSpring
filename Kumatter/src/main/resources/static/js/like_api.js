/**
 * ファイル名: like_api.js
 * 作成者: 岡本
 * 作成日: 2025-07-15
 * 概要: 
 *   投稿に対する「いいね」「いいね解除」処理をサーバーへ非同期通信で行う関数。
 *   ボタンにイベントリスナーを設定し、クリック時にfetchでAPIを呼び出す。
 *   成功時はボタン表示やいいね数を即時反映。
 */

/**
 * 対象の「いいねボタン」にAPI通信処理を設定する。
 *
 * @param {HTMLElement} button - イベントを付与するボタン要素
 */
export function setupLikeButton(button) {
  button.addEventListener('click', () => {
    // 投稿IDをdata属性から取得
    const postId = button.getAttribute('data-post-id');
    
    // ボタンのテキストが「★」ならいいね済み → 解除処理
    const isLiked = button.textContent === '★';
    const url = isLiked ? '/api/likes/remove' : '/api/likes/add';

    // CSRFトークンをmetaタグから取得
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    // サーバーへ非同期通信
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken
      },
      body: JSON.stringify({ postId: postId })
    })
    .then(response => {
      if (!response.ok) throw new Error('通信エラー');
      return response.json();
    })
    .then(data => {
      // サーバーからのレスポンスに応じて、ボタン表示を更新
      button.textContent = data.liked ? '★' : '☆';
      button.classList.toggle('liked', data.liked);

      // いいね数の表示も更新
      const likeCountSpan = button.parentElement.querySelector('.like-count');
      if (likeCountSpan) {
        likeCountSpan.textContent = data.likeCount;
      }
    })
    .catch(error => {
      // 通信失敗時の処理
      alert('いいね処理に失敗しました');
      console.error(error);
    });
  });
}