package com.example.algoyweb.repository.allen;

import com.example.algoyweb.model.entity.allen.SolvedACResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SolvedACResponseRepository extends JpaRepository<SolvedACResponseEntity, Long>{
    Optional<SolvedACResponseEntity> findByUserUsername(String username);
    Optional<SolvedACResponseEntity> findByUserEmail(String email);

    // SolvedACResponseEntity findByUserUsername(String algoyUsername);
}
