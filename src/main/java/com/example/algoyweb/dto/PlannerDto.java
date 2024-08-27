package com.example.algoyweb.dto;

import com.example.algoyweb.domain.Planner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDto {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private String quizType;

    private Planner.Status status;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
