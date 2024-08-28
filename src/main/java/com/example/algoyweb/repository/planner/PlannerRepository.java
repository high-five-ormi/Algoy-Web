package com.example.algoyweb.repository.planner;

import com.example.algoyweb.model.entity.planner.Planner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {

    @Query("select p from Planner p where p.startAt >= :startTime and p.startAt <= :endTime")
    List<Planner> findByMonth(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select p from Planner p where p.user.email = :username")
    List<Planner> findByUserEmail(String username);
}
