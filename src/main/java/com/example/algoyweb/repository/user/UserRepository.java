package com.example.algoyweb.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.algoyweb.model.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	// 이메일로 사용자를 조회하는 쿼리 메서드
	User findByEmail(String email);

	// 닉네임으로 사용자를 조회하는 쿼리 메서드
	User findByNickname(String nickname);

	// 이메일과 사용자 이름으로 사용자를 조회 후 Optional로 반환하는 쿼리 메서드
	Optional<User> findByEmailAndUsername(String email, String username);

	// 주어진 토큰으로 사용자를 조회 후 Optional로 반환하는 쿼리 메서드
	Optional<User> findByPassword(String token); // 토큰이 임시 비밀번호로 저장되었으므로 이를 통해 찾기


	List<User> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime now);
}
