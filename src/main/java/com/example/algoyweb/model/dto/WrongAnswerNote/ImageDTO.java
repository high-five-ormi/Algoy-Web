package com.example.algoyweb.model.dto.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {
    private Long id;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ImageDTO fromImage(Image image) {
        return ImageDTO.builder()
            .id(image.getId())
            .imgUrl(image.getImgUrl())
            .createdAt(image.getCreatedAt())
            .updatedAt(image.getUpdatedAt())
            .build();
    }
}
