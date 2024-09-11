package com.example.algoyweb.util.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.model.entity.user.User;

public class WrongAnswerNoteConvertUtil {

    public static WrongAnswerNoteDTO convertToDto(WrongAnswerNote entity) {
        if (entity == null) {
            return null;
        }

        WrongAnswerNoteDTO dto = new WrongAnswerNoteDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);  // User 객체에서 ID 추출
        dto.setTitle(entity.getTitle());
        dto.setLink(entity.getLink());
        dto.setQuizSite(entity.getQuizSite());
        dto.setQuizType(entity.getQuizType());
        dto.setQuizLevel(entity.getQuizLevel());
        dto.setContent(entity.getContent());
        dto.setIsSolved(entity.getIsSolved());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public static WrongAnswerNote convertToEntity(WrongAnswerNoteDTO dto, User user) {  // User 객체를 받아서 처리
        if (dto == null) {
            return null;
        }

        WrongAnswerNote entity = new WrongAnswerNote();
        entity.setId(dto.getId());
        entity.setUser(user);  // User 객체를 설정
        entity.setTitle(dto.getTitle());
        entity.setLink(dto.getLink());
        entity.setQuizSite(dto.getQuizSite());
        entity.setQuizType(dto.getQuizType());
        entity.setQuizLevel(dto.getQuizLevel());
        entity.setContent(dto.getContent());
        entity.setIsSolved(dto.getIsSolved());

        return entity;
    }

    public static void updateEntityFromDto(WrongAnswerNote entity, WrongAnswerNoteDTO dto, User user) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setUser(user);  // 업데이트 시 User도 반영
        entity.setTitle(dto.getTitle());
        entity.setLink(dto.getLink());
        entity.setQuizSite(dto.getQuizSite());
        entity.setQuizType(dto.getQuizType());
        entity.setQuizLevel(dto.getQuizLevel());
        entity.setContent(dto.getContent());
        entity.setIsSolved(dto.getIsSolved());
    }
}