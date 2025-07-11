document.addEventListener('DOMContentLoaded', () => {
  // DOMが完全に読み込まれたら実行

  const input = document.getElementById('user-search-input');        // 検索入力欄
  const suggestionList = document.getElementById('user-suggestions'); // サジェスト候補のul要素

  // CSRFトークンをmetaタグから取得（Spring Security対策）
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

  // フォローボタン押下中はinputのblurでサジェストを閉じないよう制御するフラグ
  let ignoreBlur = false;

  // フォローボタンのmousedown時（押した瞬間）にフラグを立てる
  // blurイベントとクリックイベントの競合防止用
  suggestionList.addEventListener('mousedown', (event) => {
    if (event.target.classList.contains('follow-btn')) {
      ignoreBlur = true;
    }
  });

  // ユーザー検索入力欄のinputイベント（文字入力検知）
  input.addEventListener('input', () => {
    const query = input.value.trim();

    // 2文字未満なら候補非表示＆中身クリア
    if (query.length < 2) {
      suggestionList.style.display = 'none';
      suggestionList.innerHTML = '';
      return;
    }

    // APIにGETリクエストを送りユーザー候補を取得
    fetch(`/api/users/suggest?query=${encodeURIComponent(query)}`, {
      method: 'GET',
      headers: {
        [csrfHeader]: csrfToken,  // CSRF対策トークン
        'Accept': 'application/json',
      },
      credentials: 'same-origin',
    })
      .then(response => {
        if (!response.ok) throw new Error('ネットワークエラー');
        return response.json();
      })
      .then(data => {
        suggestionList.innerHTML = '';

        // 取得候補がなければ非表示にして終了
        if (data.length === 0) {
          suggestionList.style.display = 'none';
          return;
        }

        // ユーザー候補ごとにリストアイテムを作成
        data.forEach(user => {
          const li = document.createElement('li');
          li.className = 'user-suggestion-item';

          // 名前とログインIDをまとめるdiv（左側）
          const leftInfo = document.createElement('div');
          leftInfo.className = 'user-info';

          // ユーザー名表示
          const nameSpan = document.createElement('span');
          nameSpan.textContent = user.userName;

          // ログインID表示
          const loginIdSpan = document.createElement('span');
          loginIdSpan.className = 'user-login-id';
          loginIdSpan.textContent = ` (@${user.loginId})`;

          leftInfo.appendChild(nameSpan);
          leftInfo.appendChild(loginIdSpan);

          // もしログインユーザーにフォローされていたら「フォローされています」表示を追加
          if (user.followingLoginUser) {
            const followBackSpan = document.createElement('span');
            followBackSpan.className = 'user-follow-back';
            followBackSpan.textContent = 'フォローされています';
            leftInfo.appendChild(followBackSpan);
          }

          li.appendChild(leftInfo);

          // 自分自身ではないユーザーのみフォローボタン表示
          if (!user.loginUser) {
            const followBtn = document.createElement('button');
            followBtn.className = 'follow-btn';
            followBtn.dataset.userId = user.userId;
            followBtn.textContent = user.followedByLoginUser ? 'フォロー解除' : 'フォロー';
            li.appendChild(followBtn);
          }

          // ユーザー名部分をクリックすると入力欄にセットしサジェストを閉じる
          leftInfo.addEventListener('click', () => {
            input.value = user.userName;
            suggestionList.style.display = 'none';
          });

          // 作成したリストアイテムをサジェストリストに追加
          suggestionList.appendChild(li);
        });

        // サジェストリストを表示
        suggestionList.style.display = 'block';
      })
      .catch(() => {
        // エラー時はサジェストを閉じる＆クリア
        suggestionList.style.display = 'none';
        suggestionList.innerHTML = '';
      });
  });

  // 入力欄がフォーカスアウトした時の処理（blurイベント）
  input.addEventListener('blur', () => {
    // クリックとの競合を防ぐためsetTimeoutで遅延処理
    setTimeout(() => {
      if (!ignoreBlur) {
        // フォローボタンを押していなければサジェストを閉じる
        suggestionList.style.display = 'none';
      }
      // フラグをリセット
      ignoreBlur = false;
    }, 150);
  });

  // サジェストリスト内のフォローボタンのクリックイベントを委譲で処理
  suggestionList.addEventListener('click', (event) => {
    const target = event.target;

    // フォローボタン以外は無視
    if (!target.classList.contains('follow-btn')) return;

    // 対象ユーザーIDを取得
    const userId = target.dataset.userId;
    // 現在のフォロー状態を判定（「フォロー解除」ならフォロー中）
    const isFollowing = target.textContent === 'フォロー解除';

    // フォロー解除はDELETE、フォローはPOSTでAPI呼び出し
    const method = isFollowing ? 'DELETE' : 'POST';
    const url = `/api/follows/${userId}`;

    // リクエストヘッダーにCSRFトークンとAcceptをセット
    const headers = {
      [csrfHeader]: csrfToken,
      'Accept': 'application/json',
    };
    if (method === 'POST') {
      headers['Content-Type'] = 'application/json';
    }

    // フォロー/フォロー解除APIへfetch送信
    fetch(url, {
      method: method,
      headers: headers,
      credentials: 'same-origin',
    })
      .then(res => {
        if (!res.ok) throw new Error('フォロー操作に失敗しました');

        // ボタンの文言を切り替え（フォロー中なら「フォロー解除」、それ以外は「フォロー」）
        target.textContent = isFollowing ? 'フォロー' : 'フォロー解除';
      })
      .catch(err => {
        // エラー時はアラート表示
        alert(err.message || '通信エラーが発生しました');
      });
  });
});