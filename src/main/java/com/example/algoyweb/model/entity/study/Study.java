package com.example.algoyweb.model.entity.study;

import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Study {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Participant> participants;

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> commentList;

    public enum Status {
        ING,
        DONE,
        STOP
    }

    public void update(StudyDto studyDto) {
        this.title = studyDto.getTitle();
        this.content = studyDto.getContent();
        this.language = studyDto.getLanguage();
        this.status = studyDto.getStatus();
        this.createdAt = studyDto.getCreatedAt();
        this.updatedAt = studyDto.getUpdatedAt();
    }

    public void connectComment(Comment comment) {
        if(this.commentList == null) {
            this.commentList = new ArrayList<>();
        }
        this.commentList.add(comment);
    }

    public void connectParticipants(Participant participant) {

        if(this.participants == null) {
            this.participants = new ArrayList<>();
        }
        this.participants.add(participant);
    }

    public void connectUser(User user) {
        this.user = user;
        user.connectStudy(this);
    }
}
