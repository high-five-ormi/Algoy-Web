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
        dto.setContent(entity.getContent());
        dto.setIsSolved(entity.getIsSolved());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public static WrongAnswerNote convertToEntity(WrongAnswerNoteDTO dto) {
        if (dto == null) {
            return null;
        }

        WrongAnswerNote entity = new WrongAnswerNote();
        entity.setId(dto.getId());  // Ensure ID is also set if applicable
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setLink(dto.getLink());
        entity.setQuizSite(dto.getQuizSite());
        entity.setQuizType(dto.getQuizType());
        entity.setQuizLevel(dto.getQuizLevel());
        entity.setContent(dto.getContent());
        entity.setIsSolved(dto.getIsSolved());

        return entity;
    }

    public static void updateEntityFromDto(WrongAnswerNote entity, WrongAnswerNoteDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setTitle(dto.getTitle());
        entity.setLink(dto.getLink());
        entity.setQuizSite(dto.getQuizSite());
        entity.setQuizType(dto.getQuizType());
        entity.setQuizLevel(dto.getQuizLevel());
        entity.setContent(dto.getContent());
        entity.setIsSolved(dto.getIsSolved());
    }
}