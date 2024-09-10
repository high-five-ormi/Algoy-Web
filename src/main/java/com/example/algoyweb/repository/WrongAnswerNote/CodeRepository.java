package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long> {

    List<Code> findByWrongAnswerNoteId(Long wrongAnswerNoteId);
}