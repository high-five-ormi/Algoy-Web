package com.example.algoyweb.repository.WrongAnswerNote;

import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WrongAnswerNoteRepository extends JpaRepository<WrongAnswerNote, Long> {

    // 이메일로 오답노트 조회
    @Query("SELECT w FROM WrongAnswerNote w WHERE w.id = :id AND w.user.email = :email")
    Optional<WrongAnswerNote> findByIdAndUserEmail(@Param("id") Long id, @Param("email") String email);

    // 제목으로 오답노트 검색 (사용자 정보 기반)
    @Query("SELECT w FROM WrongAnswerNote w WHERE LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')) AND w.user = :user")
    List<WrongAnswerNote> findByTitleContainingIgnoreCase(@Param("title") String title, @Param("user") User user);

    // 생성일 기준으로 정렬하여 사용자 오답노트 페이징 조회
    @Query("SELECT w FROM WrongAnswerNote w WHERE w.user = :user ORDER BY w.createdAt DESC")
    Page<WrongAnswerNote> findAllByUserOrderByCreatedAtDesc(@Param("user") User user, PageRequest pageRequest);
}