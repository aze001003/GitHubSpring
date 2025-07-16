/**
 * ファイル名: users_suggest.js
 * 作成者: 岡本
 * 作成日: 2025-07-11
 * 概要: ユーザー検索サジェスト処理（フォローAPI呼び出しは follow_api.js に委譲）
 *       - ユーザー入力に応じてサジェストを表示
 *       - サジェスト内のフォローボタン操作を非同期で処理
 */

import { toggleFollow } from './follow_api.js';
import { createFollowButton } from './follow_btn.js';

document.addEventListener('DOMContentLoaded', () => {
  // CSRFトークン関連をmetaタグから取得（Fetch APIのヘッダーに利用）
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

  // 検索入力フォームとサジェストリストのDOM取得
  const input = document.getElementById('user-search-input');
  const suggestionList = document.getElementById('user-suggestions');

  // フォローボタン押下中は入力欄のblurでサジェストを閉じないためのフラグ
  let ignoreBlur = false;

  // フォローボタン押下の瞬間（mousedown）にフラグを立てる処理
  // blurイベントとの競合回避用
  suggestionList.addEventListener('mousedown', (event) => {
    if (event.target.classList.contains('follow_btn')) {
      ignoreBlur = true;
    }
  });

  /**
   * 検索入力のinputイベントハンドラ
   * - 入力文字が2文字未満ならサジェスト非表示・クリア
   * - 2文字以上ならサジェスト候補をAPIから取得しリスト表示
   */
  input.addEventListener('input', () => {
    const query = input.value.trim();

    if (query.length < 2) {
      // 文字数不足時はサジェスト非表示かつ中身クリア
      suggestionList.style.display = 'none';
      suggestionList.innerHTML = '';
      return;
    }

    // APIへGETリクエスト送信（CSRFトークンはGETでなくても付けて問題なし）
    fetch(`/api/users/suggest?query=${encodeURIComponent(query)}`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        [csrfHeader]: csrfToken,
      },
      credentials: 'same-origin',
    })
      .then(response => {
        if (!response.ok) throw new Error('ネットワークエラー');
        return response.json();
      })
      .then(data => {
        // 取得データでサジェストリストをクリアしてから更新
        suggestionList.innerHTML = '';

        if (data.length === 0) {
          // 候補なしの場合は非表示で終了
          suggestionList.style.display = 'none';
          return;
        }

        // 各ユーザー候補をリストアイテムに変換して追加
        data.forEach(user => {
          const li = document.createElement('li');
          li.className = 'user-suggestion-item';

          // ユーザー名とログインIDをまとめる左側情報エリア
          const leftInfo = document.createElement('div');
          leftInfo.className = 'user-info';

          const nameSpan = document.createElement('span');
          nameSpan.textContent = user.userName;

          const loginIdSpan = document.createElement('span');
          loginIdSpan.className = 'user-login-id';
          loginIdSpan.textContent = ` (@${user.loginId})`;

          leftInfo.appendChild(nameSpan);
          leftInfo.appendChild(loginIdSpan);

          // ログインユーザーからフォローされている場合は「フォローされています」表示追加
          if (user.followingLoginUser) {
            const followBackSpan = document.createElement('span');
            followBackSpan.className = 'user-follow-back';
            followBackSpan.textContent = 'フォローされています';
            leftInfo.appendChild(followBackSpan);
          }

          li.appendChild(leftInfo);

          // ログインユーザー自身ではない場合のみフォローボタン表示
		  if (!user.self) {
		    const followBtn = createFollowButton(user);
		    li.appendChild(followBtn);
			console.log(user.userName, user.loginId, user.isSelf);
		  }

          // ユーザー名クリック時は入力欄に名前をセットし、サジェストを閉じる
          leftInfo.addEventListener('click', () => {
            input.value = user.userName;
            suggestionList.style.display = 'none';
          });

          // リストアイテムをサジェストリストへ追加
          suggestionList.appendChild(li);
        });

        // サジェストリストを表示
        suggestionList.style.display = 'block';
      })
      .catch(() => {
        // エラー時はサジェストを閉じ、中身をクリア
        suggestionList.style.display = 'none';
        suggestionList.innerHTML = '';
      });
  });

  /**
   * 入力欄のフォーカスアウト（blur）イベント
   * - フォローボタン押下中は無視し、それ以外はサジェストを閉じる
   * - setTimeoutでクリック処理との競合回避
   */
  input.addEventListener('blur', () => {
    setTimeout(() => {
      if (!ignoreBlur) {
        suggestionList.style.display = 'none';
      }
      ignoreBlur = false;
    }, 150);
  });
});