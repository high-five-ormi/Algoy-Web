package com.example.algoyweb.repository;

import com.example.algoyweb.domain.DemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRepository extends JpaRepository<DemoEntity, Long> {
	// 빈 파일
}