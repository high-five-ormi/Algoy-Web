package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    @Autowired
    public WrongAnswerNoteService(WrongAnswerNoteRepository wrongAnswerNoteRepository) {
        this.wrongAnswerNoteRepository = wrongAnswerNoteRepository;
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

    // 오답노트 생성
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

    // 오답노트 삭제
    public void deleteById(Long id) {
        wrongAnswerNoteRepository.deleteById(id);
    }

    // 해결 상태 업데이트
    public WrongAnswerNoteDTO updateSolvedStatus(Long id, boolean isSolved) {
        WrongAnswerNote note = wrongAnswerNoteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다."));

        note.setIsSolved(isSolved);
        WrongAnswerNote updatedNote = wrongAnswerNoteRepository.save(note);
        return WrongAnswerNoteConvertUtil.convertToDto(updatedNote);
    }
}