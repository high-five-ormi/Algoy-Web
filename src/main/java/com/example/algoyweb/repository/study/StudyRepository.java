package com.example.algoyweb.repository.study;

import com.example.algoyweb.model.entity.study.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select s from Study s where s.content like concat('%', :keyword, '%') " +
            " or s.title like concat('%', :keyword, '%') or s.language like concat('%', :keyword, '%') ")
    Page<Study> findAllByKeyword(Pageable pageable, String keyword);


    @Query("select s from Study s where s.status = :status")
    Page<Study> findAllByStatus(Pageable pageable, Study.Status status);

    Optional<Study> findByTitle(String testStudy);
}
