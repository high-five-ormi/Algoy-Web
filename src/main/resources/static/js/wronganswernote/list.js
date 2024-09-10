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

  // 검색 버튼 클릭 시 검색 요청을 처리하는 함수 추가
  const searchButton = document.querySelector('.search-button');
  const searchInput = document.querySelector('.search-input');
  const noteListSection = document.querySelector('.note-list'); // 검색 결과를 표시할 부분

  if (searchButton && searchInput) {
    searchButton.addEventListener('click', () => {
      const query = searchInput.value.trim(); // 검색 입력 필드에서 검색어 가져오기
      if (query !== '') {
        fetch(`/api/algoy/commit/search?query=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
          // 검색 결과를 동적으로 업데이트
          noteListSection.innerHTML = ''; // 기존 목록 비우기
          data.forEach(note => {
            const noteItem = document.createElement('div');
            noteItem.className = 'note-item';
            noteItem.innerHTML = `
                <h2><a href="/algoy/commit/${note.id}">${note.title}</a></h2>
                <div class="note-content">${note.content}</div>
                <div class="note-details">
                  <span><strong>링크:</strong> <a href="${note.link}" class="note-link">${note.link}</a></span>
                  <span><strong>출제 사이트:</strong> ${note.quizSite}</span>
                  <span><strong>문제 유형:</strong> ${note.quizType}</span>
                  <span><strong>문제 레벨:</strong> ${note.quizLevel}</span>
                  <span><strong>풀이 여부:</strong> ${note.isSolved ? 'Solved'
                : 'Unsolved'}</span>
                </div>
                <div class="note-dates">
                  <span>작성일: <time>${new Date(note.createdAt).toLocaleString()}</time></span>
                  <span>수정일: <time>${new Date(note.updatedAt).toLocaleString()}</time></span>
                </div>
              `;
            noteListSection.appendChild(noteItem);
          });
        })
        .catch(error => console.error('Error:', error));
      }
    });
  }
});