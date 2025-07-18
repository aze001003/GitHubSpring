/**
 * ファイル名: users_suggest.js
 * 作成者: 岡本
 * 作成日: 2025-07-11
 * 概要:
 * - ユーザー検索サジェスト機能の制御スクリプト。
 * - 入力に応じてAPIから候補を取得し、リスト表示を動的に更新。
 * - サジェスト内のフォローボタン操作は follow_api.js と follow_btn.js に委譲。
 * 
 * 機能詳細:
 * - 入力が2文字未満ならサジェスト非表示。
 * - 2文字以上でAPIから候補取得し表示。
 * - サジェスト候補にフォローボタンを表示し、非同期でフォロー操作可能。
 * - ユーザー名クリックで入力欄にセットしサジェストを閉じる。
 * - フォローボタン押下時のblurイベント競合を考慮した制御あり。
 * 
 * CSRFトークンはGETリクエストでも送付（安全側の設計）。
 * 
 * @author 岡本
 * @since 2025-07-11
 */

import { toggleFollow } from '../../api/follow_api.js';
import { createFollowButton } from '../../components/follow_btn.js';

document.addEventListener('DOMContentLoaded', () => {
  // CSRFトークン関連をmetaタグから取得（Fetch APIのヘッダーに利用）
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

  // 検索入力フォームとサジェストリストのDOM要素取得
  const input = document.getElementById('user-search-input');
  const suggestionList = document.getElementById('user-suggestions');

  // フォローボタン押下中は入力欄のblurでサジェストを閉じないためのフラグ
  let ignoreBlur = false;

  // フォローボタン押下の瞬間（mousedown）にフラグを立てる処理（blurとの競合回避）
  suggestionList.addEventListener('mousedown', (event) => {
    if (event.target.classList.contains('follow_btn')) {
      ignoreBlur = true;
    }
  });

  /**
   * 検索入力のinputイベントハンドラ
   * - 入力文字数が2文字未満ならサジェストを非表示・クリア
   * - 2文字以上ならAPIから候補を取得してリスト表示
   */
  input.addEventListener('input', () => {
    const query = input.value.trim();

    if (query.length < 2) {
      // 文字数不足時はサジェスト非表示かつ中身クリア
      suggestionList.style.display = 'none';
      suggestionList.innerHTML = '';
      return;
    }

    // APIへGETリクエスト送信（CSRFトークンをGETでも付与）
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
          // 候補なしなら非表示で終了
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

          // フォローされている場合の表示追加
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
          }

          // ユーザー名クリックで入力欄に名前をセットし、サジェストを閉じる
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
        // エラー時はサジェストを閉じ、中身クリア
        suggestionList.style.display = 'none';
        suggestionList.innerHTML = '';
      });
  });

  /**
   * 入力欄のblurイベントハンドラ
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