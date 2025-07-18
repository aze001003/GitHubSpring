/**
 * ファイル名: csrf_util.js
 * 
 * 概要:
 * - CSRFトークンを含むHTTPヘッダーを取得するユーティリティ関数。
 * - POSTメソッドの場合はContent-Typeも'application/json'に設定する。
 *
 * @param {string} method - HTTPメソッド（例: 'GET', 'POST', 'DELETE'）
 * @returns {Object} - CSRFトークンを含むHTTPリクエスト用のヘッダーオブジェクト
 *
 * 使用例:
 *   fetch('/api/like/123', {
 *     method: 'POST',
 *     headers: getCsrfHeaders('POST'),
 *     body: JSON.stringify({...})
 *   });
 *
 * 必要な前提:
 * - HTMLに以下のmetaタグが含まれていること
 *   <meta name="_csrf" content="トークン" />
 *   <meta name="_csrf_header" content="X-CSRF-TOKEN" />
 * 
 * @author 岡本
 * @since 2025-07-11
 */
export function getCsrfHeaders(method) {
  // HTMLの<meta>からCSRFトークンとヘッダー名を取得
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

  // 基本的なAcceptヘッダーとCSRFヘッダーを設定
  const headers = {
    [csrfHeader]: csrfToken,
    'Accept': 'application/json',
  };

  // POSTのときはContent-Typeを明示的に指定
  if (method === 'POST') {
    headers['Content-Type'] = 'application/json';
  }

  return headers;
}