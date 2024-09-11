package com.example.algoyweb.model.dto.WrongAnswerNote;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerNoteDTO {

    private Long id;
    private Long userId;
    private String title;
    private String link;
    private String quizSite;
    private String quizType;
    private String quizLevel;
    private String content;
    private Boolean isSolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imageUrls;
}