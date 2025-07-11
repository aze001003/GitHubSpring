/**
 * ファイル名: like_btn.js
 * 作成者: 岡本
 * 作成日: 2025-07-11
 * 概要: いいねボタンの非同期処理を行うJavaScript
 * 		 画面のリロード無しにリアルタイムでユーザーの操作を反映するために作成しました。
 */
document.addEventListener('DOMContentLoaded', () => {
  // ページ読み込み後に処理を開始

  // いいねボタン（クラス名.like-button）を全て取得してループ処理
  document.querySelectorAll('.like-button').forEach(button => {
    // 各ボタンにクリックイベントリスナーを設定
    button.addEventListener('click', () => {
      // クリックされたボタンの投稿IDをdata属性から取得
      const postId = button.getAttribute('data-post-id');
      // 現在のボタンのテキストが★なら「いいね済み」と判断
      const isLiked = button.textContent === '★';
      // ★なら「いいね解除」APIへ、☆なら「いいね追加」APIへ送信するURLを設定
      const url = isLiked ? '/api/likes/remove' : '/api/likes/add';

      // CSRFトークンをmetaタグから取得しヘッダーにセット
      const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

      // fetch APIで非同期POSTリクエストを送信
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json', // JSON形式で送信
          'X-CSRF-TOKEN': csrfToken           // CSRFトークンのセット
        },
        body: JSON.stringify({ postId: postId }) // 投稿IDをJSON形式で送信
      })
      .then(response => {
        // 通信結果のレスポンスがOKでなければエラーを投げる
        if (!response.ok) throw new Error('通信エラー');
        return response.json(); // レスポンスをJSONとして解析
      })
      .then(data => {
        // レスポンスの liked フラグに応じてボタンのテキストを★または☆に切替
        button.textContent = data.liked ? '★' : '☆';
        // liked クラスの付与・削除で見た目を切り替え（色など）
        button.classList.toggle('liked', data.liked);

        // いいね数表示用の要素(.like-count)を取得し、最新のいいね数に更新
        const likeCountSpan = button.parentElement.querySelector('.like-count');
        if (likeCountSpan) {
          likeCountSpan.textContent = data.likeCount;
        }
      })
      .catch(error => {
        // 通信失敗時のエラーハンドリング。アラート表示とコンソールログ出力
        alert('いいね処理に失敗しました');
        console.error(error);
      });
    });
  });
});