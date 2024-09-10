// 'New' 버튼 클릭 시 리다이렉트
function redirectToCreate() {
  window.location.href = '/algoy/commit/create';
}

document.addEventListener('DOMContentLoaded', () => {
  const noteContents = document.querySelectorAll('.note-content');

  noteContents.forEach(content => {
    // HTML 태그 제거
    content.innerHTML = content.innerText;
  });
});