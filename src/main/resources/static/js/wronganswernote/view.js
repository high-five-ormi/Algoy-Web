document.addEventListener('DOMContentLoaded', function () {
  const addCodeButton = document.getElementById('add-code-button');
  const codeModal = document.getElementById('codeModal');
  const submitCode = document.getElementById('submitCode');
  const cancelCode = document.getElementById('cancelCode');
  const codeInput = document.getElementById('codeInput');
  const sampleCodeContainer = document.querySelector('.sample-code-container');
  const noteDetails = document.querySelector('.note-details');
  const editCodeModal = document.getElementById('editCodeModal');
  const editCodeInput = document.getElementById('editCodeInput');
  const submitEditCode = document.getElementById('submitEditCode');
  const cancelEditCode = document.getElementById('cancelEditCode');

  let currentCodeId = null; // 현재 수정 중인 코드 ID

  const noteId = document.querySelector('.edit-btn')?.getAttribute('data-note-id');
  const apiBaseUrl = '/api/codes';

  if (noteId) {
    // 페이지 로드 시 기존 코드 블록을 가져옵니다.
    fetch(`${apiBaseUrl}/${noteId}`)
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        data.codes.forEach(code => {
          addCodeBlock(code);
        });
      } else {
        console.error('Failed to load codes.');
      }
    })
    .catch(error => console.error('Error loading codes:', error));
  }

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
  if (submitCode && codeInput && sampleCodeContainer && noteId) {
    submitCode.addEventListener('click', function () {
      const codeContent = codeInput.value.trim();
      if (codeContent) {
        fetch(`${apiBaseUrl}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ codeContent, wrongAnswerNote: { id: noteId } })
        })
        .then(response => response.json())
        .then(data => {
          if (data.success) {
            addCodeBlock(data.code);
            codeModal.style.display = 'none';
            codeInput.value = ''; // 입력 필드 초기화
          } else {
            console.error('Failed to add code.');
          }
        })
        .catch(error => console.error('Error adding code:', error));
      }
    });
  }

  // 코드 블록 추가
  function addCodeBlock(code) {
    const codeBlock = document.createElement('div');
    codeBlock.classList.add('sample-code');
    codeBlock.innerHTML = `
      <h3>코드</h3>
      <pre><code>${code.codeContent}</code></pre>
      <button class="edit-code-btn" data-code-id="${code.id}">수정</button>
      <button class="delete-code-btn" data-code-id="${code.id}">삭제</button>
    `;
    sampleCodeContainer.appendChild(codeBlock);
  }

  // 코드 블록 수정 버튼 클릭 시
  if (sampleCodeContainer) {
    sampleCodeContainer.addEventListener('click', function (event) {
      if (event.target.classList.contains('edit-code-btn')) {
        currentCodeId = event.target.getAttribute('data-code-id');
        if (currentCodeId) {
          const codeContent = event.target.closest('.sample-code').querySelector('code').textContent;
          editCodeInput.value = codeContent;
          editCodeModal.style.display = 'flex';
        }
      } else if (event.target.classList.contains('delete-code-btn')) {
        if (confirm('정말 삭제하시겠습니까?')) {
          const codeId = event.target.getAttribute('data-code-id');
          if (codeId) {
            fetch(`${apiBaseUrl}/${codeId}`, {
              method: 'DELETE',
              headers: {
                'Content-Type': 'application/json'
              }
            })
            .then(response => {
              if (response.ok) {
                event.target.closest('.sample-code').remove();
              } else {
                alert('삭제에 실패했습니다.');
              }
            })
            .catch(error => console.error('Error deleting code:', error));
          }
        }
      }
    });
  }

  // 수정 모달 닫기 (취소 버튼)
  if (cancelEditCode && editCodeModal && editCodeInput) {
    cancelEditCode.addEventListener('click', function () {
      editCodeModal.style.display = 'none';
      editCodeInput.value = ''; // 입력 필드 초기화
    });
  }

  // 코드 수정 (수정 버튼)
  if (submitEditCode && editCodeInput) {
    submitEditCode.addEventListener('click', function () {
      const codeContent = editCodeInput.value.trim();
      if (codeContent && currentCodeId) {
        fetch(`${apiBaseUrl}/${currentCodeId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ codeContent })
        })
        .then(response => response.json())
        .then(data => {
          if (data.success) {
            const codeBlock = sampleCodeContainer.querySelector(`.edit-code-btn[data-code-id="${currentCodeId}"]`).closest('.sample-code');
            codeBlock.querySelector('code').textContent = data.code.codeContent;
            editCodeModal.style.display = 'none';
            editCodeInput.value = ''; // 입력 필드 초기화
          } else {
            console.error('Failed to edit code.');
          }
        })
        .catch(error => console.error('Error editing code:', error));
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