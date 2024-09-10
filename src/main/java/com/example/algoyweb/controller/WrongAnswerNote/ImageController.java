package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.service.WrongAnswerNote.ImageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/algoy")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<ImageDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("빈 파일이 업로드 시도됨");
            return ResponseEntity.badRequest().body(null);
        }

        try {
            ImageDTO imageDTO = imageService.uploadImage(file);
            logger.info("이미지 업로드 성공: URL = {}", imageDTO.getImgUrl());
            return ResponseEntity.ok(imageDTO);
        } catch (Exception e) {
            logger.error("이미지 업로드 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 이미지 URL 조회
    @GetMapping("/image/{id}")
    public ResponseEntity<String> getImageUrl(@PathVariable Long id) {
        try {
            String url = imageService.getImageUrl(id);
            logger.info("이미지 조회 성공: ID = {}, URL = {}", id, url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            logger.error("이미지 조회 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }
}