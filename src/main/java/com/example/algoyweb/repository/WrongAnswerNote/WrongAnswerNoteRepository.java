package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WrongAnswerNoteRepository extends JpaRepository<WrongAnswerNote, Long> {
    // 페이지네이션과 정렬을 지원하는 메소드
    @Query("SELECT w FROM WrongAnswerNote w ORDER BY w.createdAt DESC")
    Page<WrongAnswerNote> findAllOrderByCreatedDateDesc(Pageable pageable);
}