package com.example.algoyweb.repository.planner;

import com.example.algoyweb.model.entity.planner.Planner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {

    @Query("select p from Planner p where p.startAt >= :startTime and p.startAt <= :endTime")
    List<Planner> findByMonth(LocalDate startTime, LocalDate endTime);

    @Query("select p from Planner p where p.user.email = :username")
    List<Planner> findByUserEmail(String username);

    @Query("select p from Planner p where p.title like concat('%', :keyword, '%') " +
            "or p.questionName like concat('%', :keyword, '%') ")
    List<Planner> findByKeyword(String keyword);
}
