package com.example.algoyweb.repository.allen;

import com.example.algoyweb.model.entity.allen.SolvedACResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SolvedACResponseRepository extends JpaRepository<SolvedACResponse, Long>{
    Optional<SolvedACResponse> findByUserUsername(String username);
}
