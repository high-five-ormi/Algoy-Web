package com.example.algoyweb.util;

import com.example.algoyweb.model.dto.comment.CommentDto;
import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.study.Comment;
import com.example.algoyweb.model.entity.planner.Planner;
import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.model.entity.study.Study;
import com.example.algoyweb.model.entity.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

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
        .questionName(planner.getQuestionName())
        .etcName(planner.getEtcName())
        .siteName(planner.getSiteName())
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
        .siteName(plannerDto.getSiteName())
        .etcName(plannerDto.getEtcName())
        .questionName(plannerDto.getQuestionName())
        .build();
  }

  public static UserDto convertUserToDto(User findUser) {
    return UserDto.builder()
        .role(findUser.getRole())
        .username(findUser.getUsername())
        .email(findUser.getEmail())
        .nickname(findUser.getNickname())
        .userId(findUser.getUserId())
            .solvedacUserName(findUser.getSolvedacUserName()) // solvedac UserName 추가
        .isDeleted(findUser.getIsDeleted())
        .createdAt(findUser.getCreatedAt())
        .updatedAt(findUser.getUpdatedAt())
        .deletedAt(findUser.getDeletedAt())
        .build();
  }

  public static StudyDto convertStudyToDto(Study study) {
    return StudyDto.builder()
        .language(study.getLanguage())
        .title(study.getTitle())
        .content(study.getContent())
        .status(study.getStatus())
            .author(study.getUser().getNickname())
            .id(study.getId())
            .maxParticipant(study.getMaxParticipant())
        .createdAt(study.getCreatedAt())
        .updatedAt(study.getUpdatedAt())
        .build();
  }

  public static Study convertDtoToStudy(StudyDto studyDto) {
    return Study.builder()
        .content(studyDto.getContent())
        .title(studyDto.getTitle())
        .status(studyDto.getStatus())
        .createdAt(studyDto.getCreatedAt())
        .updatedAt(studyDto.getUpdatedAt())
        .language(studyDto.getLanguage())
            .maxParticipant(studyDto.getMaxParticipant())
        .build();
  }

  public static CommentDto convertCommentToDto(Comment save) {
    return CommentDto.builder()
            .id(save.getId())
            .content(save.getContent())
            .createdAt(save.getCreatedAt())
            .updatedAt(save.getUpdatedAt())
            .depth(save.getDepth())
            .secret(save.getSecret())
            .author(save.getUser().getNickname())
            .build();
  }
}
