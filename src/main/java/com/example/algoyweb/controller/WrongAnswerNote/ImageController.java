package com.example.algoyweb.controller.WrongAnswerNote;

import com.example.algoyweb.service.WrongAnswerNote.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/algoy")
public class ImageController {

    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일을 선택해주세요.");
        }

        try {
            String fileUrl = imageService.uploadImage(file).getImgUrl();
            logger.info("이미지 업로드 성공: URL = {}", fileUrl);
            return ResponseEntity.ok("이미지가 성공적으로 업로드되었습니다. URL: " + fileUrl);
        } catch (Exception e) {
            logger.error("이미지 업로드 중 서버 오류 발생", e);
            throw e;
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<String> getImageUrl(@PathVariable Long id) {
        try {
            String url = imageService.getImageUrl(id);
            logger.info("이미지 조회 성공: ID = {}, URL = {}", id, url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            logger.error("이미지 조회 중 서버 오류 발생", e);
            throw e;
        }
    }
}