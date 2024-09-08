package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.Image;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.ImageRepository;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public WrongAnswerNoteService(WrongAnswerNoteRepository wrongAnswerNoteRepository, ImageRepository imageRepository) {
        this.wrongAnswerNoteRepository = wrongAnswerNoteRepository;
        this.imageRepository = imageRepository;
    }

    // 오답노트 페이징 조회
    public Page<WrongAnswerNoteDTO> findAllWithPagination(int page, int size) {
        return wrongAnswerNoteRepository.findAllOrderByCreatedDateDesc(PageRequest.of(page, size))
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    // 오답노트 아이디로 조회
    public Optional<WrongAnswerNoteDTO> findById(Long id) {
        return wrongAnswerNoteRepository.findById(id)
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    // 오답노트 생성 및 이미지 저장
    public WrongAnswerNoteDTO save(WrongAnswerNoteDTO dto) {
        WrongAnswerNote wrongAnswerNote = WrongAnswerNoteConvertUtil.convertToEntity(dto);
        WrongAnswerNote savedNote = wrongAnswerNoteRepository.save(wrongAnswerNote);
        return WrongAnswerNoteConvertUtil.convertToDto(savedNote);
    }

    // 오답노트 수정
    public WrongAnswerNoteDTO update(Long id, WrongAnswerNoteDTO dto) {
        WrongAnswerNote wrongAnswerNote = wrongAnswerNoteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다."));

        WrongAnswerNoteConvertUtil.updateEntityFromDto(wrongAnswerNote, dto);
        WrongAnswerNote updatedNote = wrongAnswerNoteRepository.save(wrongAnswerNote);
        return WrongAnswerNoteConvertUtil.convertToDto(updatedNote);
    }

    public void linkImagesToNote(Long noteId, List<String> imageUrls) {
        WrongAnswerNote note = wrongAnswerNoteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다."));

        List<Image> images = imageRepository.findAllByImgUrlIn(imageUrls);
        images.forEach(image -> image.setWrongAnswerNote(note));
        imageRepository.saveAll(images);
    }

    // 오답노트 삭제
    public void deleteById(Long id) {
        wrongAnswerNoteRepository.deleteById(id);
    }

    // 이미지 저장 메소드
    private List<Image> saveImages(List<MultipartFile> images, WrongAnswerNote wrongAnswerNote) throws IOException {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream().map(image -> {
            Image img = new Image();
            img.setWrongAnswerNote(wrongAnswerNote);
            // 파일 저장 로직 추가 (예: 로컬 경로 또는 클라우드 업로드)
            img.setImgUrl("/uploads/images/" + image.getOriginalFilename()); // 예시 URL 설정
            return img;
        }).collect(Collectors.toList());
    }

    public WrongAnswerNoteDTO updateSolvedStatus(Long id, boolean isSolved) {
        WrongAnswerNote note = wrongAnswerNoteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다."));

        note.setIsSolved(isSolved);
        WrongAnswerNote updatedNote = wrongAnswerNoteRepository.save(note);
        return WrongAnswerNoteConvertUtil.convertToDto(updatedNote);
    }
}