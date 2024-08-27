package com.example.algoyweb.utils;

import com.example.algoyweb.domain.Planner;
import com.example.algoyweb.dto.PlannerDto;

import java.time.LocalDateTime;

public class ConvertUtils {

    public static PlannerDto convertPlannerToDto(Planner planner) {
        return PlannerDto.builder()
                .id(planner.getId())
                .title(planner.getTitle())
                .endAt(planner.getEndAt())
                .startAt(planner.getStartAt())
                .content(planner.getContent())
                .createAt(planner.getCreateAt())
                .quizType(planner.getQuizType())
                .status(planner.getStatus())
                .updateAt(planner.getUpdateAt())
                .build();
    }

    public static Planner convertDtoToPlanner(PlannerDto plannerDto) {

        return Planner.builder()
                .content(plannerDto.getContent())
                .createAt(LocalDateTime.now())
                .status(plannerDto.getStatus())
                .updateAt(LocalDateTime.now())
                .endAt(plannerDto.getEndAt())
                .title(plannerDto.getTitle())
                .startAt(plannerDto.getStartAt())
                .quizType(plannerDto.getQuizType())
                .build();
    }
}
