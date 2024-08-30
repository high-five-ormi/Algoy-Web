package com.example.algoyweb.model.dto.planner;

import com.example.algoyweb.model.entity.planner.Planner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDto {

    private Long id;

    private String title;

    private String content;

    private LocalDate startAt;

    private LocalDate endAt;

    private String link;

    private Planner.Status status;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
