package com.example.algoyweb.util.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;

public class ImageConvertUtil {

    public static ImageDTO convertToDto(Image image) {
        if (image == null) {
            return null;
        }
        return ImageDTO.builder()
            .id(image.getId())
            .imgUrl(image.getImgUrl())
            .createdAt(image.getCreatedAt())
            .build();
    }

    public static Image convertToEntity(ImageDTO dto, WrongAnswerNote wrongAnswerNote, String originFileName, String storeFileName) {
        if (dto == null) {
            return null;
        }
        Image image = new Image();
        image.setId(dto.getId());
        image.setImgUrl(dto.getImgUrl());
        image.setCreatedAt(dto.getCreatedAt());
        image.setWrongAnswerNote(wrongAnswerNote); // 연관된 WrongAnswerNote 설정
        image.setOriginFileName(originFileName);   // 파일 이름 설정
        image.setStoreFileName(storeFileName);     // 저장된 파일 이름 설정
        return image;
    }
}
