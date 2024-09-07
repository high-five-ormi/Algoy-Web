package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrongAnswerNoteRepository extends JpaRepository<WrongAnswerNote, Long> {
    // 페이지네이션 지원
    Page<WrongAnswerNote> findAll(Pageable pageable);
}