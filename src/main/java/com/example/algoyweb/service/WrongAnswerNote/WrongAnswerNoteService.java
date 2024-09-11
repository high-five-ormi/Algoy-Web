package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;
    private final UserRepository userRepository;

    @Autowired
    public WrongAnswerNoteService(WrongAnswerNoteRepository wrongAnswerNoteRepository,
        UserRepository userRepository) {
        this.wrongAnswerNoteRepository = wrongAnswerNoteRepository;
        this.userRepository = userRepository;
    }

    // 사용자별로 오답노트 페이징 조회
    public Page<WrongAnswerNoteDTO> findAllWithPagination(int page, int size, String userEmail) {
        // 사용자 조회
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        // 사용자 오답노트를 페이징하여 조회
        return wrongAnswerNoteRepository.findAllByUserOrderByCreatedAtDesc(user,
                PageRequest.of(page, size))
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    // 사용자별로 ID로 오답노트 조회
    public Optional<WrongAnswerNoteDTO> findById(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return wrongAnswerNoteRepository.findByIdAndUserEmail(id, userEmail)
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    // 오답노트 생성
    public WrongAnswerNoteDTO save(WrongAnswerNoteDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        WrongAnswerNote wrongAnswerNote = WrongAnswerNoteConvertUtil.convertToEntity(dto, user);
        WrongAnswerNote savedNote = wrongAnswerNoteRepository.save(wrongAnswerNote);
        return WrongAnswerNoteConvertUtil.convertToDto(savedNote);
    }

    // 오답노트 수정
    public WrongAnswerNoteDTO update(Long id, WrongAnswerNoteDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        WrongAnswerNote wrongAnswerNote = wrongAnswerNoteRepository.findByIdAndUserEmail(id,
                userEmail)
            .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다."));

        WrongAnswerNoteConvertUtil.updateEntityFromDto(wrongAnswerNote, dto, user);
        WrongAnswerNote updatedNote = wrongAnswerNoteRepository.save(wrongAnswerNote);
        return WrongAnswerNoteConvertUtil.convertToDto(updatedNote);
    }

    // 오답노트 삭제
    public void deleteById(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        WrongAnswerNote wrongAnswerNote = wrongAnswerNoteRepository.findByIdAndUserEmail(id,
                userEmail)
            .orElseThrow(() -> new IllegalArgumentException("해당 오답노트를 찾을 수 없습니다."));
        wrongAnswerNoteRepository.delete(wrongAnswerNote);
    }

    // 검색 기능: 사용자별 제목으로 오답노트 검색
    public List<WrongAnswerNoteDTO> search(String query, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return wrongAnswerNoteRepository.findByTitleContainingIgnoreCase(query, user).stream()
            .map(WrongAnswerNoteConvertUtil::convertToDto)
            .collect(Collectors.toList());
    }
}