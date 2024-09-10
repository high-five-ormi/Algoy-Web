document.addEventListener('DOMContentLoaded', () => {
  const isSolvedInput = document.getElementById('isSolved');
  const statusDisplay = document.getElementById('isSolved-status-display');

  // "풀이 여부" 초기 표시 처리
  if (isSolvedInput && statusDisplay) {
    statusDisplay.textContent = `풀이 여부: ${isSolvedInput.value}`;

    // "풀이 여부" 변경 시 처리
    isSolvedInput.addEventListener('change', (e) => {
      const status = e.target.value;
      statusDisplay.textContent = `풀이 여부: ${status}`;
    });
  }

  // Quill 에디터 초기화
  const quill = new Quill('#editor', {
    theme: 'snow',
    modules: {
      toolbar: [
        [{ 'font': [] }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        ['bold', 'italic', 'underline'],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'align': [] }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }]
      ]
    }
  });

  // 기존 내용 로드
  const existingContent = document.querySelector('input[name="content"]').value;
  if (existingContent) {
    try {
      const delta = quill.clipboard.convert(existingContent);
      quill.setContents(delta, 'silent');
    } catch (error) {
      console.error('Failed to load existing content into Quill:', error);
      quill.root.innerHTML = existingContent;
    }
  }

  // 폼 제출 전에 Quill 에디터의 내용을 hidden 필드에 저장
  document.querySelector('form').addEventListener('submit', () => {
    document.querySelector('input[name="content"]').value = quill.root.innerHTML;
  });
});