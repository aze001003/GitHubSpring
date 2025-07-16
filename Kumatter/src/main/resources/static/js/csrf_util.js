/**
 * CSRFトークンを含むHTTPヘッダーを取得するユーティリティ関数。
 * POSTメソッドの場合はContent-Typeも'application/json'に設定する。
 *
 * @param {string} method HTTPメソッド（例: 'GET', 'POST', 'DELETE'）
 * @returns {Object} CSRFトークンを含むヘッダーオブジェクト
 * 
 * @author 岡本
 * @since 2025-07-11
 */
export function getCsrfHeaders(method) {
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
  const headers = {
    [csrfHeader]: csrfToken,
    'Accept': 'application/json',
  };
  if (method === 'POST') {
    headers['Content-Type'] = 'application/json';
  }
  return headers;
}