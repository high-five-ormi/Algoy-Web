package com.example.algoyweb.util;

import com.example.algoyweb.model.entity.planner.Planner;
import com.example.algoyweb.model.dto.planner.PlannerDto;

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
                .status(planner.getStatus())
                .updateAt(planner.getUpdateAt())
                .link(planner.getLink())
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
                .link(plannerDto.getLink())
                .build();
    }
}
