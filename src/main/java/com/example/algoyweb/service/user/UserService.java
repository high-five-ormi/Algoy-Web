package com.example.algoyweb.service.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.UserErrorCode;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.util.ConvertUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder; // Spring Security의 PasswordEncoder 사용

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * 회원가입 처리
	 *
	 * @author yuseok
	 * @param userDto 회원가입 정보를 담고 있는 DTO
	 */
	@Transactional
	public void signUpUser(UserDto userDto) {
		// User 엔티티 생성
		User user = User.builder()
			.username(userDto.getUsername())
			.nickname(userDto.getNickname())
			.email(userDto.getEmail())
			.password(passwordEncoder.encode(userDto.getPassword())) // 비밀번호 암호화
			.role(Role.NORMAL)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.build();

		// 저장
		userRepository.save(user);
	}

	// 이메일로 사용자 찾기
	@Transactional
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	// 닉네임으로 사용자 찾기
	@Transactional
	public User findByNickname(String nickname) {
		return userRepository.findByNickname(nickname);
	}

	/**
	 * 로그인
	 *
	 * @param email 로그인시 email로 로그인
	 * @return 저장된 사용자 정보를 담은 UserDto
	 * @author jooyoung
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + email);
		}

		return org.springframework.security.core.userdetails.User
			.withUsername(user.getEmail())
			.password(user.getPassword())  // Assuming this is already hashed
			.authorities(new SimpleGrantedAuthority(user.getRole().getKey())) // 권한 추가
			.build();
	}

	@Transactional
	public UserDto update(UserDto userDto, String email) {
		User findUser = userRepository.findByEmail(email);

		if (findUser == null) {
			throw new NoSuchElementException("No user found with the given email: " + email);
		}

		if (!Objects.equals(userDto.getEmail(), email)) {
			throw new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL);
		}

		// 비밀번호 암호화 처리
		String encodedPassword = null;
		if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
			encodedPassword = passwordEncoder.encode(userDto.getPassword());
		}

		// UserDto에서 업데이트 정보를 반영
		findUser.updateUser(userDto, encodedPassword);

		// Save the updated user entity
		userRepository.save(findUser);

		return ConvertUtils.convertUserToDto(findUser);
	}

	/**
	 * 탈퇴 신청
	 *
	 * @param email 로그인시 email로 로그인
	 * @return user를 repository에 저장
	 * @author jooyoung
	 */
	@Transactional
	public void setDeleted(String email, HttpServletRequest request, HttpServletResponse response) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.setDeleted();
			userRepository.save(user); // 변경 사항 저장

			// 로그아웃 처리
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
			SecurityContextHolder.clearContext();

			// 명시적으로 쿠키 삭제
			Cookie rememberMeCookie = new Cookie("remember-me", null);
			rememberMeCookie.setPath("/");
			rememberMeCookie.setMaxAge(0);
			response.addCookie(rememberMeCookie);

			Cookie sessionCookie = new Cookie("JSESSIONID", null);
			sessionCookie.setPath("/");
			sessionCookie.setMaxAge(0);
			response.addCookie(sessionCookie);
		}
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional
	public void delete(String username) {
		User user = userRepository.findByEmail(username);
		if (user == null || !user.getIsDeleted()) {
			throw new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL);
		}

		userRepository.delete(user);
	}

	/**
	 * 계정 삭제 스케줄러
	 *
	 * @return user를 삭제
	 * @author jooyoung
	 * 확인 필요합니다.
	 */
	@Transactional
	//@Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
	@Scheduled(fixedRate = 86400000) // 매일 실행 (24시간 = 86400000 ms)
	public void deleteScheduledUsers() {
		List<User> usersToDelete = userRepository.findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime.now());
		for (User user : usersToDelete) {
			userRepository.delete(user);
		}
	}

	/**
	 * 계정 복구
	 *
	 * @param email 로그인시 email로 로그인
	 * @return user를 repository에 저장
	 * @author jooyoung
	 */
	public void restoreAccount(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.restore();
			userRepository.save(user); // 변경 사항 저장
		}
	}


	/*// 로그인 여부를 확인
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated()
			&& !(authentication instanceof AnonymousAuthenticationToken);
	}*/

	/**
	 * 사용자의 이메일과 사용자 이름으로 비밀번호 재설정 토큰 생성
	 *
	 * @author yuseok
	 * @param email 사용자의 이메일
	 * @param username 사용자의 사용자 이름
	 * @return 비밀번호 재설정에 사용할 토큰, 사용자 정보가 일치하지 않으면 null 반환
	 */
	public String findPassword(String email, String username) {
		// 이메일과 사용자 이름으로 해당 유저 찾기
		Optional<User> userOptional = userRepository.findByEmailAndUsername(email, username);

		// 유저를 찾으면
		if (userOptional.isPresent()) { // isPresent(): Optional 객체가 값을 포함하고 있는지 확인 (객체에 값이 있으면 true, 비어있으면 false 반환)
			// 비밀번호 재설정을 위한 임시 토큰 생성
			String token = UUID.randomUUID().toString();
			User user = userOptional.get();
			user.updatePassword(token); // updatePassword 메서드를 사용해 토큰을 임시 비밀번호로 저장
			userRepository.save(user);
			return token;
		}
		return null;
	}

	/**
	 * 주어진 토큰을 사용하여 사용자의 비밀번호 재설정
	 *
	 * @author yuseok
	 * @param token 비밀번호 재설정에 필요한 토큰
	 * @param newPassword 사용자가 입력한 새 비밀번호
	 * @return 비밀번호 재설정에 성공하면 true, 실패하면 false 반환
	 */
	public boolean resetPassword(String token, String newPassword) {
		// 임시 비밀번호(토큰)으로 유저 찾기
		Optional<User> userOptional = userRepository.findByPassword(token);

		if (userOptional.isPresent()) { // 유저가 존재하면
			User user = userOptional.get();
			user.updatePassword(passwordEncoder.encode(newPassword)); // 새로운 비밀번호로 업데이트
			userRepository.save(user);
			return true;
		}

		return false; // 유저가 없거나 토큰이 잘못된 경우 실패
	}

	public List<UserDto> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {
			UserDto userDto = ConvertUtils.convertUserToDto(user);
			userDtos.add(userDto);
		}

		return userDtos;
	}

	// 관리자 승격
	public void promoteToAdmin(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 권한이 NORMAL일 경우 ADMIN으로 승격
		if (user.getRole() == Role.NORMAL) {
			user.updateRole(Role.ADMIN);
			userRepository.save(user);
		}
	}

	// 유저 밴
	public void banUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 권한이 NORMAL인 경우 BANNED로 변경
		if (user.getRole() == Role.NORMAL) {
			user.updateRole(Role.BANNED);
			userRepository.save(user);
		}
	}

	// 유저 밴 해제
	public void liftBan(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 권한이 BANNED인 경우 NORMAL로 변경
		if (user.getRole() == Role.BANNED) {
			user.updateRole(Role.NORMAL);
			userRepository.save(user);
		}
	}
}