package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long> {
    // 특정 오답노트에 속한 코드 블록 찾기
    List<Code> findByWrongAnswerNoteId(Long wrongAnswerNoteId);
}