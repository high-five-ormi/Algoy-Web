package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WrongAnswerNoteRepository extends JpaRepository<WrongAnswerNote, Long> {
    // 페이지네이션과 정렬을 지원하는 메소드
    @Query("SELECT w FROM WrongAnswerNote w ORDER BY w.createdAt DESC")
    Page<WrongAnswerNote> findAllOrderByCreatedDateDesc(Pageable pageable);

    // 제목이나 내용에 특정 키워드를 포함한 오답노트를 검색하는 메소드
    @Query("SELECT w FROM WrongAnswerNote w WHERE LOWER(w.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY w.createdAt DESC")
    List<WrongAnswerNote> findByTitleContainingIgnoreCase(@Param("query") String query);
}