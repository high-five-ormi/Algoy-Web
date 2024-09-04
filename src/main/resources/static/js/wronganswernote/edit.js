document.addEventListener('DOMContentLoaded', () => {
  const dropZone = document.getElementById('drop-zone');
  const fileInput = document.getElementById('files');
  const fileNames = document.getElementById('file-names');
  const imagePreviews = document.getElementById('image-previews');
  let selectedFiles = new DataTransfer();

  function addFiles(newFiles) {
    const filesArray = Array.from(newFiles);

    filesArray.forEach(file => {
      if (!selectedFiles.items.some(item => item.getAsFile().name === file.name)) {
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

  fileInput.addEventListener('change', (e) => {
    addFiles(e.target.files);
  });
});