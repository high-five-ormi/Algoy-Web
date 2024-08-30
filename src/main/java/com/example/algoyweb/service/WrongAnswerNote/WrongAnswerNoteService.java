package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository repository;

    public WrongAnswerNoteService(WrongAnswerNoteRepository repository) {
        this.repository = repository;
    }

    // 모든 오답 노트 조회
    public List<WrongAnswerNoteDTO> findAll() {
        return repository.findAll().stream()
            .map(WrongAnswerNoteConvertUtil::convertToDto) // Entity -> DTO 변환
            .collect(Collectors.toList());
    }

    // ID로 오답 노트 조회
    public Optional<WrongAnswerNoteDTO> findById(Long id) {
        return repository.findById(id)
            .map(WrongAnswerNoteConvertUtil::convertToDto); // Entity -> DTO 변환
    }

    // 오답 노트 저장 및 업데이트
    public void save(WrongAnswerNoteDTO dto) {
        WrongAnswerNote entity = WrongAnswerNoteConvertUtil.convertToEntity(dto); // DTO -> Entity 변환
        if (dto.getId() == null) {
            // 새로운 게시물일 때 생성 시간 설정
            entity.setCreatedAt(LocalDateTime.now());
        }
        // 수정 시간은 항상 업데이트
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity); // Entity 저장
    }

    // ID로 오답 노트 삭제
    public void deleteById(Long id) {
        repository.deleteById(id); // Repository를 통한 삭제
    }
}