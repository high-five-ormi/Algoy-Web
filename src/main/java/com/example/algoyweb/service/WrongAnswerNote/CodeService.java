package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Code;
import com.example.algoyweb.repository.WrongAnswerNote.CodeRepository;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeService {
    private final CodeRepository codeRepository;
    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    // 특정 오답노트의 코드 가져오기
    public List<Code> getCodesByWrongAnswerNoteId(Long wrongAnswerNoteId) {
        return codeRepository.findByWrongAnswerNoteId(wrongAnswerNoteId);
    }

    // 코드 저장
    public Code saveCode(Code code) {
        return codeRepository.save(code);
    }

    // 코드 수정
    public Code updateCode(Long codeId, Code updatedCode) {
        return codeRepository.findById(codeId)
            .map(existingCode -> {
                existingCode.setCodeContent(updatedCode.getCodeContent());
                return codeRepository.save(existingCode);
            })
            .orElseThrow(() -> new IllegalArgumentException("코드를 찾을 수 없습니다: " + codeId));
    }

    // 코드 삭제
    public boolean deleteCode(Long codeId) {
        if (codeRepository.existsById(codeId)) {
            codeRepository.deleteById(codeId);
            return true;
        }
        return false;
    }
}