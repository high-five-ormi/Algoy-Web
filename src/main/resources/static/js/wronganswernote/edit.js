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
  const contentField = document.querySelector('input[name="content"]');
  const existingContent = contentField.value;
  console.log('Existing content:', existingContent);

  if (existingContent) {
    try {
      const delta = quill.clipboard.convert(existingContent);
      quill.setContents(delta, 'silent');
    } catch (error) {
      console.error('Failed to load existing content into Quill:', error);
      quill.root.innerHTML = existingContent;
    }
  }

  console.log('Quill contents after initialization:', quill.root.innerHTML);

  // Quill 에디터 내용 변경 시 hidden 필드 업데이트
  quill.on('text-change', function() {
    contentField.value = quill.root.innerHTML;
    console.log('Quill content changed:', contentField.value);
  });

  // 폼 제출 처리
  document.querySelector('form').addEventListener('submit', (event) => {
    event.preventDefault();

    // Quill 에디터의 내용을 hidden 필드에 저장
    contentField.value = quill.root.innerHTML;
    console.log('Form is being submitted. Content:', contentField.value);

    // FormData를 사용하여 폼 데이터 수집
    const formData = new FormData(event.target);

    // AJAX를 사용하여 폼 제출
    fetch(event.target.action, {
      method: 'POST',
      body: formData
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      console.log('Success:', data);
      // 성공 시 리다이렉트 또는 다른 처리
      // 예: window.location.href = '/success-page';
    })
    .catch((error) => {
      console.error('Error:', error);
      // 에러 처리
    });
  });
});