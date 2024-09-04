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

// 폼 제출 시 빈 필드 확인
document.getElementById('noteForm').addEventListener('submit', (event) => {
  const title = document.getElementById('title').value.trim();
  const link = document.getElementById('link').value.trim();
  const quizSite = document.getElementById('quizSite').value.trim();
  const quizType = document.getElementById('quizType').value.trim();
  const quizLevel = document.getElementById('quizLevel').value.trim();
  const content = document.getElementById('content').value.trim();

  if (!title || !link || !quizSite || !quizType || !quizLevel || !content) {
    event.preventDefault(); // 폼 제출 막기
    alert('모든 필드를 채워주세요.'); // 사용자에게 알림
  }
});