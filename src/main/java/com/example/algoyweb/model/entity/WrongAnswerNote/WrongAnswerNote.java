package com.example.algoyweb.model.entity.WrongAnswerNote;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wrong_answer_note")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String link;
    private String quizSite;
    private String quizType;
    private String quizLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content;
    private Boolean isSolved;
}