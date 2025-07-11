document.addEventListener('DOMContentLoaded', () => {
  // DOMの読み込み完了後に実行

  // ユーザーサジェストのul要素を取得
  const suggestionList = document.getElementById('user-suggestions');

  // CSRFトークンをmetaタグから取得（ヘッダー名とトークン）
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

  // サジェストリスト内のクリックイベントを一括で処理（イベント委譲）
  suggestionList.addEventListener('click', (event) => {
    const target = event.target;

    // クリックされた要素がフォローボタン（クラス follow_btn）でなければ処理しない
    if (!target.classList.contains('follow_btn')) return;

    // ボタンに紐付いたユーザーIDを取得
    const userId = target.dataset.userId;
    // 現在フォロー中かどうかの状態を取得（'true' or 'false' の文字列）
    const isFollowing = target.dataset.following === 'true';
    // HTTPメソッドはフォロー解除ならDELETE、フォローならPOSTを指定
    const method = isFollowing ? 'DELETE' : 'POST';

    // リクエストヘッダーにCSRFトークンとAcceptをセット
    const headers = {
      [csrfHeader]: csrfToken,
      'Accept': 'application/json',
    };
    // フォローする場合のみContent-Typeヘッダーを付加（JSON形式送信）
    if (method !== 'DELETE') {
      headers['Content-Type'] = 'application/json';
    }

    // APIへフォロー・フォロー解除リクエストを送信
    fetch(`/api/follows/${userId}`, {
      method: method,
      headers: headers,
      credentials: 'same-origin', // Cookieを送信
    })
      .then(res => {
        // レスポンスが正常でなければ例外をスロー
        if (!res.ok) throw new Error('フォロー操作に失敗しました');

        // ボタン表示を切り替え
        target.textContent = isFollowing ? 'フォロー' : 'フォロー解除';
        // フォロー状態のdata属性も更新（文字列で保存）
        target.dataset.following = (!isFollowing).toString();
      })
      .catch(err => {
        // エラー時はアラート表示とログ出力
        alert(err.message || '通信エラーが発生しました');
      });
  });
});