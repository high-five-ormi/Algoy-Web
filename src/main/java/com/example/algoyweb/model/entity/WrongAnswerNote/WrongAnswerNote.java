package com.example.algoyweb.model.entity.WrongAnswerNote;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String content;
    private Boolean isSolved;

    @OneToMany(mappedBy = "wrongAnswerNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isSolved = false; // 기본값 설정
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}