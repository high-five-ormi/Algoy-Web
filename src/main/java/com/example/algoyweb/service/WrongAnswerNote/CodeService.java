package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Code;
import com.example.algoyweb.repository.WrongAnswerNote.CodeRepository;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CodeService {
    private final CodeRepository codeRepository;
    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    public CodeService(CodeRepository codeRepository, WrongAnswerNoteRepository wrongAnswerNoteRepository) {
        this.codeRepository = codeRepository;
        this.wrongAnswerNoteRepository = wrongAnswerNoteRepository;
    }

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
        Optional<Code> existingCodeOpt = codeRepository.findById(codeId);
        if (existingCodeOpt.isPresent()) {
            Code existingCode = existingCodeOpt.get();
            existingCode.setCodeContent(updatedCode.getCodeContent());
            return codeRepository.save(existingCode);
        }
        return null;  // 코드가 존재하지 않으면 null 반환
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