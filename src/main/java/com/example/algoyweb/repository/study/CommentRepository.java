package com.example.algoyweb.repository.study;

import com.example.algoyweb.model.entity.study.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.study.id = :studyId")
    List<Comment> findAllByStudyId(Long studyId);
}
