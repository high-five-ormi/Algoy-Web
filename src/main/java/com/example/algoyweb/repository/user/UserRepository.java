package com.example.algoyweb.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.algoyweb.model.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  // 이메일로 사용자를 조회하는 쿼리 메서드
  User findByEmail(String email);

  // 닉네임으로 사용자를 조회하는 쿼리 메서드
  User findByNickname(String nickname);
}
