package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.ImageRepository;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    @Value("${upload.directory}")
    private String uploadDir;

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public ImageDTO uploadImage(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String storeFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = Paths.get(uploadDir).resolve(storeFileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);

        Image image = new Image();
        image.setOriginFileName(originalFileName);
        image.setStoreFileName(storeFileName);
        image.setFilePath(filePath.toString());
        image.setImgUrl("/uploads/" + storeFileName);

        logger.info("이미지 파일 저장 완료: 원본 파일명 = {}, 저장 파일명 = {}, 경로 = {}", originalFileName, storeFileName, filePath.toString());

        Image savedImage = imageRepository.save(image);
        return ImageDTO.fromImage(savedImage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void linkImageToNote(Long noteId, Long imageId) {
        logger.info("linkImageToNote 호출됨: noteId = {}, imageId = {}", noteId, imageId);

        try {
            WrongAnswerNote note = wrongAnswerNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다. Note ID: " + noteId));

            Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다. Image ID: " + imageId));

            image.setWrongAnswerNote(note);
            imageRepository.save(image);

            logger.info("이미지와 오답노트 연결 완료: Note ID = {}, Image ID = {}", noteId, imageId);
        } catch (Exception e) {
            logger.error("이미지와 오답노트 연결 중 오류 발생", e);
            throw e;
        }
    }

    public String getImageUrl(Long imageId) {
        return imageRepository.findById(imageId)
            .map(Image::getImgUrl)
            .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다: " + imageId));
    }
}