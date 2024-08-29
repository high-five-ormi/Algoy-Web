package com.example.algoyweb.util.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;

public class WrongAnswerNoteConvertUtil {

    public static WrongAnswerNoteDTO convertToDto(WrongAnswerNote entity) {
        if (entity == null) {
            return null;
        }
        WrongAnswerNoteDTO dto = new WrongAnswerNoteDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setLink(entity.getLink());
        dto.setQuizSite(entity.getQuizSite());
        dto.setQuizType(entity.getQuizType());
        dto.setQuizLevel(entity.getQuizLevel());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setContent(entity.getContent());
        dto.setIsSolved(entity.getIsSolved());
        return dto;
    }

    public static WrongAnswerNote convertToEntity(WrongAnswerNoteDTO dto) {
        if (dto == null) {
            return null;
        }
        WrongAnswerNote entity = new WrongAnswerNote();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setLink(dto.getLink());
        entity.setQuizSite(dto.getQuizSite());
        entity.setQuizType(dto.getQuizType());
        entity.setQuizLevel(dto.getQuizLevel());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setContent(dto.getContent());
        entity.setIsSolved(dto.getIsSolved());
        return entity;
    }
}