package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    @Value("${upload.directory}")
    private String uploadDir;

    public ImageDTO uploadImage(MultipartFile file) throws IOException {
        // 기존 로직과 유사하지만 WrongAnswerNote 참조 제거
        String originalFileName = file.getOriginalFilename();
        String storeFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = Paths.get(uploadDir).resolve(storeFileName);

        Files.copy(file.getInputStream(), filePath);

        Image image = new Image();
        image.setOriginFileName(originalFileName);
        image.setStoreFileName(storeFileName);
        image.setFilePath(filePath.toString());
        image.setImgUrl("/uploads/" + storeFileName);

        Image savedImage = imageRepository.save(image);
        return ImageDTO.fromImage(savedImage);
    }

    // 이미지 URL 조회 메서드 추가
    public String getImageUrl(Long imageId) {
        return imageRepository.findById(imageId)
            .map(Image::getImgUrl)
            .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다: " + imageId));
    }
}