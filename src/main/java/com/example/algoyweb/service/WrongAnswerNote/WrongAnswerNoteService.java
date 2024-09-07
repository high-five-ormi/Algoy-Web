package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository repository;
    private final ImageService imageService;

    // 페이지네이션을 고려한 WrongAnswerNote 조회 메소드
    public Page<WrongAnswerNoteDTO> findAllWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable).map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    // 모든 WrongAnswerNote를 조회하는 메소드 (기존)
    public List<WrongAnswerNoteDTO> findAll() {
        return repository.findAll().stream()
            .map(WrongAnswerNoteConvertUtil::convertToDto)
            .collect(Collectors.toList());
    }

    // 특정 ID로 WrongAnswerNote를 조회하는 메소드 (기존)
    public Optional<WrongAnswerNoteDTO> findById(Long id) {
        return repository.findById(id)
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    // WrongAnswerNote를 저장하는 메소드 (기존)
    public WrongAnswerNoteDTO save(WrongAnswerNoteDTO dto, List<MultipartFile> images) throws IOException {
        WrongAnswerNote entity = WrongAnswerNoteConvertUtil.convertToEntity(dto);

        if (dto.getIsSolved() == null) {
            entity.setIsSolved(false);  // 기본값 설정
        } else {
            entity.setIsSolved(dto.getIsSolved());
        }

        repository.save(entity);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                imageService.uploadImage(image, entity);
            }
        }

        return WrongAnswerNoteConvertUtil.convertToDto(entity);
    }

    // WrongAnswerNote를 업데이트하는 메소드 (기존)
    public WrongAnswerNoteDTO update(Long id, WrongAnswerNoteDTO dto, List<MultipartFile> images) throws IOException {
        WrongAnswerNote entity = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entity not found"));

        WrongAnswerNoteConvertUtil.updateEntityFromDto(entity, dto);
        repository.save(entity);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                imageService.uploadImage(image, entity);
            }
        }

        return WrongAnswerNoteConvertUtil.convertToDto(entity);
    }

    // 특정 ID의 WrongAnswerNote를 삭제하는 메소드 (기존)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}