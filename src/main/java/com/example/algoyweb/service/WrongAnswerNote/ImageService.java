package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.exception.ImageNotFoundException;
import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import com.example.algoyweb.repository.WrongAnswerNote.ImageRepository;
import com.example.algoyweb.util.WrongAnswerNote.ImageConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private static final String UPLOAD_DIR = "uploads/wrong_answer_note/";

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public ImageDTO createImage(ImageDTO imageDTO) {
        Image image = ImageConvertUtil.convertToEntity(imageDTO);
        image.setCreatedAt(LocalDateTime.now());
        image = imageRepository.save(image);
        return ImageConvertUtil.convertToDto(image);
    }

    public ImageDTO uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일을 선택해주세요.");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());

            String fileUrl = "/wrong_answer_note/" + fileName;
            Image image = new Image();
            image.setImgUrl(fileUrl);
            image.setCreatedAt(LocalDateTime.now());
            image = imageRepository.save(image);

            return ImageConvertUtil.convertToDto(image);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public String getImageUrl(Long id) {
        return imageRepository.findById(id)
            .map(Image::getImgUrl)
            .orElseThrow(() -> new ImageNotFoundException("ID " + id + "에 해당하는 이미지를 찾을 수 없습니다."));
    }

    public void deleteImageFile(String imgUrl) {
        try {
            String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}