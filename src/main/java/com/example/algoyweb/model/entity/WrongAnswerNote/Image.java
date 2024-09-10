package com.example.algoyweb.model.entity.WrongAnswerNote;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wrong_answer_note_id")
    @JsonBackReference
    private WrongAnswerNote wrongAnswerNote;

    @Column(nullable = false)
    private String originFileName;

    @Column(nullable = false)
    private String storeFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String imgUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}