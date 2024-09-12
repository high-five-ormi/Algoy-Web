package com.example.algoyweb.model.entity.study;

import com.example.algoyweb.model.dto.comment.CommentDto;
import com.example.algoyweb.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    private Boolean secret;

    @ColumnDefault("0")
    private Boolean isDeleted;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ColumnDefault("0")
    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @Builder.Default
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Participant> participants;

    public void connectUser(User findUser) {
        this.user = findUser;
        findUser.connectComment(this);
    }

    public void connectStudy(Study findStudy) {
        this.study = findStudy;
        findStudy.connectComment(this);
    }

    public void connectParticipants(Participant participant) {

        if(this.participants == null) {
            this.participants = new ArrayList<>();
        }
        this.participants.add(participant);
    }

    public void connectParent(Comment findParent) {
        this.parent = findParent;
        findParent.getChildren().add(this);
    }

    public void update(CommentDto commentDto) {
        this.content = commentDto.getContent();
        this.secret = commentDto.getSecret();
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
    }
}
