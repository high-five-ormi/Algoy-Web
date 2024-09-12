package com.example.algoyweb.repository.study;

import com.example.algoyweb.model.entity.study.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("select count(p) from Participant p where p.study.id = :studyId")
    Integer countByStudyId(Long studyId);

    @Query("select p from Participant p where p.comment.user.userId = :userId and p.study.id = :id")
    Optional<Participant> findByUserIdAndStudyId(Long userId, Long id);

    @Query("select exists(select p from Participant p where p.comment.user.userId = :userId and p.study.id = :id)")
    Boolean existsByUserIdAndStudyId(Long userId, Long id);
}
