/**
 * ファイル名: timeline_switch.js
 * 概要:
 * - タイムラインの表示モード切替（「フォロー中」「全体」）タブの制御を担当するスクリプト。
 * - タブクリック時に指定のコールバックを呼び出し、UI表示切り替えを行う。
 * 
 * @param {string} initialMode - 初期表示モード（例: 'followed' または 'all'）
 * @param {function} onModeChange - モード変更時に呼ばれるコールバック関数(mode:string)
 * 
 * @author 岡本
 * @since 2025-07-17
 */
export function setupTimelineSwitch(initialMode, onModeChange) {
  let currentMode = initialMode;

  // data-mode属性付きのタブボタン要素群を取得
  const tabButtons = document.querySelectorAll('.tab-button');

  /**
   * タブの見た目(active/inactive)と投稿リストの表示切替を行う
   * @param {string} mode - 現在の表示モード
   */
  function updateActiveTab(mode) {
    tabButtons.forEach(button => {
      if (button.dataset.mode === mode) {
        button.classList.add('tab-active');
        button.classList.remove('tab-inactive');
      } else {
        button.classList.remove('tab-active');
        button.classList.add('tab-inactive');
      }
    });

    // 投稿リストの表示切替（クラス 'active' で切り替え）
    const followedList = document.querySelector('.followed-list');
    const allList = document.querySelector('.all-list');

    if (mode === 'followed') {
      followedList.classList.add('active');
      allList.classList.remove('active');
    } else if (mode === 'all') {
      allList.classList.add('active');
      followedList.classList.remove('active');
    }
  }

  // 初期表示のタブ設定
  updateActiveTab(currentMode);

  // タブボタンクリック時の処理登録
  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      const selectedMode = button.dataset.mode;
      if (selectedMode && selectedMode !== currentMode) {
        currentMode = selectedMode;
        updateActiveTab(currentMode);
        onModeChange(currentMode); // 画面側の描画処理等を呼び出す
      }
    });
  });
}