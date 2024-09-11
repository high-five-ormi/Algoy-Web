package com.example.algoyweb.model.entity.WrongAnswerNote;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(length = 50)
    private String title;

    @Column(length = 2083)
    private String link;

    @Column(name = "quiz_site", length = 50)
    private String quizSite;

    @Column(name = "quiz_type", length = 50)
    private String quizType;

    @Column(name = "quiz_level", length = 50)
    private String quizLevel;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_solved")
    private Boolean isSolved;

    @OneToMany(mappedBy = "wrongAnswerNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Code> codes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}