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
        // DTO에서는 생성일과 수정일을 제외합니다.
        // 엔티티에서 자동으로 처리되기 때문에 DTO에서 값을 설정하지 않습니다.
        return dto;
    }

    public static WrongAnswerNote convertToEntity(WrongAnswerNoteDTO dto) {
        if (dto == null) {
            return null;
        }
        WrongAnswerNote entity = new WrongAnswerNote();
        entity.setId(dto.getId());  // ID는 업데이트 시에만 사용
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setLink(dto.getLink());
        entity.setQuizSite(dto.getQuizSite());
        entity.setQuizType(dto.getQuizType());
        entity.setQuizLevel(dto.getQuizLevel());
        entity.setContent(dto.getContent());
        entity.setIsSolved(dto.getIsSolved());
        // DTO에서 날짜를 설정하지 않고, 엔티티에서 자동으로 처리되도록 합니다.
        return entity;
    }
}