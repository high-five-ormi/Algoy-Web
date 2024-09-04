package com.example.algoyweb.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.UserErrorCode;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.util.ConvertUtils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
		System.out.println(user);

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
			.password(user.getPassword()) // Assuming this is already hashed
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

	@Transactional
	public void setDeleted(String email) {
		User findUser = userRepository.findByEmail(email);
		findUser.setDeleted();
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional
	public void delete(String username) {
		User findUser = userRepository.findByEmail(username);
		if (!findUser.getIsDeleted()) {
			throw new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL);
		}
		userRepository.delete(findUser);
	}

	public String getUserNicknameByEmail(String email) {
		User user = userRepository.findByEmail(email); // 이메일을 기준으로 사용자를 조회
		if (user != null) {
			return user.getNickname(); // 닉네임 반환
		}
		return null; // 사용자가 없으면 null 반환
	}

	// 로그인 여부를 확인
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated()
			&& !(authentication instanceof AnonymousAuthenticationToken);
	}

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
}