package com.example.algoyweb.model.entity.study;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    public void connectStudy(Study findStudy) {
        this.study = findStudy;
        findStudy.connectParticipants(this);
    }

    public void connectComment(Comment findComment) {
        this.comment = findComment;
        this.userId = findComment.getUser().getUserId();
        findComment.connectParticipants(this);
    }
}
