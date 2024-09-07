package com.example.algoyweb.model.dto.study;

import com.example.algoyweb.model.entity.study.Study;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDto {

    private String title;

    private String content;

    private String language;

    private Study.Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
