package com.example.algoyweb.service.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionService {
	private final UserRepository userRepository;

	// 관리자 승격
	public void promoteToAdmin(Long userId) {
		// 유저 ID로 유저를 조회하고, 없으면 예외 던지기
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 권한이 NORMAL일 경우 ADMIN으로 승격
		if (user.getRole() == Role.NORMAL) {
			user.updateRole(Role.ADMIN);
			userRepository.save(user);
		}
	}

	// 유저 밴
	public void banUser(Long userId, String banReason) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 유저가 정지 상태인지 확인 후, 정지 상태인 경우 예외 던지기
		if (user.isBanned()) {
			throw new IllegalStateException("해당 유저는 이미 정지된 상태입니다." + user.getBanExpiration());
		}

		if (user.getRole() == Role.ADMIN) { // 관리자는 정지 불가
			throw new IllegalStateException("관리자는 정지할 수 없습니다." + user.getRole());
		}

		int banCount = user.getBanCount(); // 해당 유저의 현재 정지 횟수
		LocalDateTime banExpiration; // 정지 만료 시간

		// 정지 횟수에 따라 정지 기간 결정
		if (banCount == 0) { // 첫 번째 정지면 1일
			banExpiration = LocalDateTime.now().plusDays(1); // 1일 10시 정지면 2일 10시에 해제
		} else if (banCount == 1) { // 두 번째 정지는 7일
			banExpiration = LocalDateTime.now().plusDays(7);
		} else { // 세 번째 이후 정지는 15일
			banExpiration = LocalDateTime.now().plusDays(15);
		}

		// 유저 정지
		user.updateRole(Role.BANNED);
		// 유저의 정지 횟수 1 증가
		user.increaseBanCount();
		// 정지 사유 입력
		user.updateBanReason(banReason);
		// 계산된 정지 만료일을 유저 객체에 설정
		user.updateBanExpiration(banExpiration);

		userRepository.save(user); // 변경된 유저 정보를 DB에 저장
	}

	// 유저 밴 해제
	public void liftBan(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 권한이 BANNED인 경우 NORMAL로 변경
		if (user.getRole() == Role.BANNED) {
			user.updateRole(Role.NORMAL);
			user.updateBanReason(null);
			user.updateBanExpiration(null); // 밴 만료 시간 초기화
			userRepository.save(user);
		}
	}

	@Scheduled(fixedRate = 60000) // 이전 작업 시작 후 1분마다 실행
	// @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
	public void unbanExpiredUsers() {
		try {
			log.info("Starting unban process...");
			// BANNED 상태인 모든 유저 조회
			List<User> bannedUsers = userRepository.findAllByRole(Role.BANNED);

			// 정지 상태인 모든 유저에 대해 반복 처리
			for (User user : bannedUsers) {
				// 유저의 정지 만료 시간이 현재 시간보다 이전인지 확인 (정지 해제 대상인지 확인)
				if (user.getBanExpiration() != null && LocalDateTime.now().isAfter(user.getBanExpiration())) {
					// 정지 만료 시간이 지났으면 정지 해제
					user.updateRole(Role.NORMAL);
					user.updateBanReason(null);
					user.updateBanExpiration(null);
					userRepository.save(user);
				} else {
					log.debug("아직 정지 기간이 만료되지 않았습니다.");
				}
			}
		} catch (Exception e) {
			log.error("Error occurred during unban process", e);
		}
	}
}