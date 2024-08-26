package com.example.algoyweb.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.algoyweb.model.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  // 쿼리 메서드
}