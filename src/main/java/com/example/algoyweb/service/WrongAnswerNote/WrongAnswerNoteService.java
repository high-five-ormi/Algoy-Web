package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    public List<WrongAnswerNoteDTO> findAll() {
        return repository.findAll().stream()
            .map(WrongAnswerNoteConvertUtil::convertToDto)
            .collect(Collectors.toList());
    }

    public Optional<WrongAnswerNoteDTO> findById(Long id) {
        return repository.findById(id)
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    public WrongAnswerNoteDTO save(WrongAnswerNoteDTO dto, List<MultipartFile> images) throws IOException {
        WrongAnswerNote entity = WrongAnswerNoteConvertUtil.convertToEntity(dto);

        // isSolved 필드 설정 (optional)
        if (dto.getIsSolved() == null) {
            entity.setIsSolved(false); // 기본값 설정
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

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}