const dropZone = document.getElementById('drop-zone');
const fileInput = document.getElementById('files');
const fileNames = document.getElementById('file-names');
const imagePreviews = document.getElementById('image-previews');
let selectedFiles = new DataTransfer();

function addFiles(newFiles) {
  for (let file of newFiles) {
    selectedFiles.items.add(file);

    // 이미지 파일 미리보기 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      displayImagePreview(e.target.result, file.name);
    };
    reader.readAsDataURL(file);
  }
  fileInput.files = selectedFiles.files;
  updateFileNames();
}

function updateFileNames() {
  fileNames.innerHTML = '';
  for (let i = 0; i < fileInput.files.length; i++) {
    fileNames.innerHTML += `<p>선택된 파일 ${i + 1}: ${fileInput.files[i].name}</p>`;
  }
}

function displayImagePreview(imageDataUrl, fileName) {
  const previewImage = document.createElement('img');
  previewImage.src = imageDataUrl;
  previewImage.alt = fileName;
  previewImage.className = 'image-preview';
  imagePreviews.appendChild(previewImage);
}

// 드래그 앤 드롭 이벤트 처리
dropZone.addEventListener('dragover', (e) => {
  e.preventDefault();
  dropZone.classList.add('dragover');
});

dropZone.addEventListener('dragleave', () => {
  dropZone.classList.remove('dragover');
});

dropZone.addEventListener('drop', (e) => {
  e.preventDefault();
  dropZone.classList.remove('dragover');
  addFiles(e.dataTransfer.files);
});

// 파일 선택 이벤트 처리
fileInput.addEventListener('change', (e) => {
  addFiles(e.target.files);
});

// 폼 제출 처리
document.getElementById('noteForm').addEventListener('submit',
    async (event) => {
      event.preventDefault(); // 기본 폼 제출 막기

      const formData = new FormData(event.target);

      try {
        const response = await fetch('/algoy/commit/create', {
          method: 'POST',
          body: formData,
        });

        if (response.ok) {
          // 성공적으로 게시글이 작성되면 리스트 페이지로 리다이렉트
          window.location.href = '/algoy/commit';
        } else {
          // 에러 처리
          const errorMessage = await response.text();
          alert(`게시글 작성에 실패했습니다: ${errorMessage}`);
        }
      } catch (error) {
        console.error('Error:', error);
        alert('서버와의 통신 중 오류가 발생했습니다.');
      }
    });
