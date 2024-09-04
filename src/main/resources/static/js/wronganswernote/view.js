document.addEventListener('DOMContentLoaded', function () {
  const addCodeButton = document.getElementById('add-code-button');
  const codeModal = document.getElementById('codeModal');
  const submitCode = document.getElementById('submitCode');
  const cancelCode = document.getElementById('cancelCode');
  const codeInput = document.getElementById('codeInput');
  const sampleCodeContainer = document.querySelector('.sample-code-container');
  const noteDetails = document.querySelector('.note-details');

  // 모달 열기
  if (addCodeButton && codeModal) {
    addCodeButton.addEventListener('click', function () {
      codeModal.style.display = 'flex';
    });
  }

  // 모달 닫기 (취소 버튼)
  if (cancelCode && codeModal && codeInput) {
    cancelCode.addEventListener('click', function () {
      codeModal.style.display = 'none';
      codeInput.value = ''; // 입력 필드 초기화
    });
  }

  // 코드 추가 (추가 버튼)
  if (submitCode && codeInput && sampleCodeContainer) {
    submitCode.addEventListener('click', function () {
      const codeContent = codeInput.value.trim();
      if (codeContent) {
        // 코드 블록을 동적으로 추가
        const codeBlock = document.createElement('div');
        codeBlock.classList.add('sample-code');
        codeBlock.innerHTML = `
          <h3>코드</h3>
          <pre><code>${codeContent}</code></pre>
          <button class="delete-code-btn">삭제</button>
        `;
        sampleCodeContainer.appendChild(codeBlock);
        codeModal.style.display = 'none';
        codeInput.value = ''; // 입력 필드 초기화
      }
    });
  }

  // 코드 블록 삭제 버튼 클릭 시
  if (sampleCodeContainer) {
    sampleCodeContainer.addEventListener('click', function (event) {
      if (event.target.classList.contains('delete-code-btn')) {
        if (confirm('정말 삭제하시겠습니까?')) {
          const codeBlock = event.target.closest('.sample-code');
          if (codeBlock) {
            codeBlock.remove();
          }
        }
      }
    });
  }

  // 수정 및 삭제 버튼 클릭 시
  if (noteDetails) {
    noteDetails.addEventListener('click', function (event) {
      if (event.target.classList.contains('edit-btn')) {
        const noteId = event.target.getAttribute('data-note-id');
        if (noteId) {
          window.location.href = `/algoy/commit/${noteId}/edit`;
        }
      } else if (event.target.classList.contains('delete-btn')) {
        const noteId = event.target.getAttribute('data-note-id');
        if (noteId && confirm('정말 삭제하시겠습니까?')) {
          fetch(`/api/algoy/commit/${noteId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json'
            }
          })
          .then(response => {
            if (response.ok) {
              window.location.href = '/algoy/commit'; // 삭제 후 리다이렉션
            } else {
              alert('삭제에 실패했습니다.');
            }
          })
          .catch(error => console.error('Error:', error));
        }
      }
    });
  }
});