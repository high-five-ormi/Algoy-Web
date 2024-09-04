package com.example.algoyweb.util.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.ImageDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.Image;

public class ImageConvertUtil {

    public static ImageDTO convertToDto(Image image) {
        if (image == null) {
            return null;
        }
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setImgUrl(image.getImgUrl());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }

    public static Image convertToEntity(ImageDTO dto) {
        if (dto == null) {
            return null;
        }
        Image image = new Image();
        image.setId(dto.getId());
        image.setImgUrl(dto.getImgUrl());
        image.setCreatedAt(dto.getCreatedAt());
        return image;
    }
}