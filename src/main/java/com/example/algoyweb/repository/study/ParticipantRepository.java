package com.example.algoyweb.repository.study;

import com.example.algoyweb.model.entity.study.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("select p from Participant p where p.study.id = :studyId and p.comment.id = :commentId")
    Optional<Participant> findByCommentAndStudy(Long commentId, Long studyId);
}
