package com.example.algoyweb.model.dto.comment;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private Long parent;

    private String content;

    private Boolean secret;

    private Boolean isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int depth;

    private List<CommentDto> children;

    private String userEmail;

    private String author;
}
