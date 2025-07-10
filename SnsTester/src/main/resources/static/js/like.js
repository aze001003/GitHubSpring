document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.like-button').forEach(button => {
    button.addEventListener('click', () => {
      const postId = button.getAttribute('data-post-id');
      const isLiked = button.textContent === '★';  // ★かどうかで判定
      const url = isLiked ? '/api/likes/remove' : '/api/likes/add';

      const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

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
        // ボタンは☆か★だけ表示。いいね数は別の要素を更新
        button.textContent = data.liked ? '★' : '☆';
        button.classList.toggle('liked', data.liked);

        const likeCountSpan = button.parentElement.querySelector('.like-count');
        if (likeCountSpan) {
          likeCountSpan.textContent = data.likeCount;
        }
      })
      .catch(error => {
        alert('いいね処理に失敗しました');
        console.error(error);
      });
    });
  });
});