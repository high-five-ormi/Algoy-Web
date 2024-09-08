package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 이미지 URL 리스트로 이미지 엔티티를 조회하는 메소드 추가
    List<Image> findAllByImgUrlIn(List<String> imgUrls);
}