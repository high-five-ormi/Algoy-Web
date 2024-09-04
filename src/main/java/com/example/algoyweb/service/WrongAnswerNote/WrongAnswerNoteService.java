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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository repository;

    public WrongAnswerNoteService(WrongAnswerNoteRepository repository) {
        this.repository = repository;
    }

    public List<WrongAnswerNoteDTO> findAll() {
        return repository.findAll().stream()
            .map(WrongAnswerNoteConvertUtil::convertToDto)
            .collect(Collectors.toList());
    }

    public Optional<WrongAnswerNoteDTO> findById(Long id) {
        return repository.findById(id)
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    public WrongAnswerNoteDTO save(WrongAnswerNoteDTO dto) {
        WrongAnswerNote entity = WrongAnswerNoteConvertUtil.convertToEntity(dto);
        if (dto.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        WrongAnswerNote savedEntity = repository.save(entity);
        return WrongAnswerNoteConvertUtil.convertToDto(savedEntity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public WrongAnswerNoteDTO update(Long id, WrongAnswerNoteDTO dto) {
        WrongAnswerNote entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entity not found"));
        // 유틸 메서드를 사용해 DTO의 값을 엔티티에 업데이트
        WrongAnswerNoteConvertUtil.updateEntityFromDto(entity, dto);
        WrongAnswerNote updatedEntity = repository.save(entity);
        return WrongAnswerNoteConvertUtil.convertToDto(updatedEntity);
    }
}