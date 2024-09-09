document.addEventListener('DOMContentLoaded', () => {
  const dropZone = document.getElementById('drop-zone');
  const fileInput = document.getElementById('files');
  const fileNames = document.getElementById('file-names');
  const imagePreviews = document.getElementById('image-previews');
  const isSolvedInput = document.getElementById('isSolved');
  const statusDisplay = document.getElementById('isSolved-status-display');
  let selectedFiles = new DataTransfer();

  // 파일을 미리보기와 파일 리스트에 추가하는 함수
  function addFiles(newFiles) {
    const filesArray = Array.from(newFiles);

    filesArray.forEach(file => {
      // 중복 파일 방지
      if (!Array.from(selectedFiles.files).some(existingFile => existingFile.name === file.name)) {
        selectedFiles.items.add(file);

        const fileReader = new FileReader();
        fileReader.onload = () => {
          const imgElement = document.createElement('img');
          imgElement.src = fileReader.result;
          imagePreviews.appendChild(imgElement);
        };
        fileReader.readAsDataURL(file);
      }
    });

    fileInput.files = selectedFiles.files;
    fileNames.innerHTML = Array.from(fileInput.files).map(file => `<p>${file.name}</p>`).join('');
  }

  // 드래그 앤 드롭 파일 업로드를 위한 이벤트 리스너
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

  // 파일 선택 변경 시 처리하는 이벤트 리스너
  fileInput.addEventListener('change', (e) => {
    addFiles(e.target.files);
  });

  // "풀이 여부" 초기 표시 처리
  if (isSolvedInput && statusDisplay) {
    statusDisplay.textContent = `풀이 여부: ${isSolvedInput.value}`;

    // "풀이 여부" 변경 시 처리
    isSolvedInput.addEventListener('change', (e) => {
      const status = e.target.value;
      statusDisplay.textContent = `풀이 여부: ${status}`;
    });
  }
});