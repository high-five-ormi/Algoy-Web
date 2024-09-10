document.addEventListener('DOMContentLoaded', () => {
  const isSolvedInput = document.getElementById('isSolved');
  const fileInput = document.getElementById('files');

  var quill = new Quill('#editor', {
    theme: 'snow',
    modules: {
      toolbar: {
        container: [
          [{ header: [1, 2, false] }],
          ['bold', 'italic', 'underline'],
          ['link'],
          [{ list: 'ordered' }, { list: 'bullet' }],
          [{ script: 'sub' }, { script: 'super' }],
          [{ indent: '-1' }, { indent: '+1' }],
          [{ color: [] }, { background: [] }],
          [{ align: [] }],
          ['image'],
          ['clean']
        ],
        handlers: {
          image: imageHandler
        }
      }
    }
  });

  // 이미지 삽입 핸들러
  function imageHandler() {
    fileInput.click();
  }

  // 파일 선택 후 이미지 처리
  fileInput.addEventListener('change', async function () {
    const files = fileInput.files;
    if (files.length > 0) {
      const file = files[0];

      const maxFileNameLength = 50;
      let fileName = file.name;
      if (fileName.length > maxFileNameLength) {
        const extension = fileName.substring(fileName.lastIndexOf('.'));
        fileName = fileName.substring(0, maxFileNameLength - extension.length) + extension;
      }

      const formData = new FormData();
      formData.append('image', file, fileName);

      try {
        const response = await fetch('/upload-image-endpoint', {
          method: 'POST',
          body: formData,
        });

        if (response.ok) {
          const result = await response.json();
          const imageUrl = result.url;

          const range = quill.getSelection();
          quill.insertEmbed(range.index, 'image', imageUrl);
        } else {
          alert('이미지 업로드에 실패했습니다.');
        }
      } catch (error) {
        console.error('이미지 업로드 중 오류 발생:', error);
        alert('이미지 업로드 중 오류가 발생했습니다.');
      }
    }
  });

  // 폼 제출 처리
  document.getElementById('noteForm').addEventListener('submit', async function (event) {

    event.preventDefault();

    const content = document.querySelector('#content');
    content.value = quill.root.innerHTML;

    // 선택된 풀이 여부 값 확인
    const isSolved = isSolvedInput.value; // 현재 선택된 값을 가져옵니다.
    console.log('풀이 여부 값:', isSolved); // 디버깅을 위한 로그

    const formData = new FormData(this);
    formData.set('isSolved', isSolved); // FormData에 현재 선택된 값을 명확하게 설정

    try {
      const response = await fetch('/algoy/commit/create', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        window.location.href = '/algoy/commit';
      } else {
        const errorMessage = await response.text();
        console.error('서버 오류:', errorMessage);
        alert(`게시글 작성에 실패했습니다: ${errorMessage}`);
      }
    } catch (error) {
      console.error('전송 중 오류:', error);
      alert('서버와의 통신 중 오류가 발생했습니다.');
    }
  });
});