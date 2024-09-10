package com.example.algoyweb.service.user;

import static com.example.algoyweb.model.entity.user.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import com.example.algoyweb.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Spy
	@InjectMocks
	private UserService userService;

	private UserDto userDto;
	private User existingUser;

	@BeforeEach
	public void setUp() {
		userDto = UserDto.builder()
			.username("test username")
			.nickname("test nickname")
			.email("test@example.com")
			.password("password123")
			.role(NORMAL)
			.isDeleted(false)
			.build();

		existingUser = User.builder()
				.username("existing username")
				.nickname("existing nickname")
				.email("test@example.com")
				.password("encodedPassword")
				.role(NORMAL)
				.isDeleted(false)
				.build();
	}

	@Test
	void testSignUpUser() {
		String encodedPassword = "encodedPassword";
		when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);

		// signUpUser 메서드 실행
		userService.signUpUser(userDto);

		// ArgumentCaptor를 사용하여 UserRepository에 저장된 User 객체를 캡처
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		verify(userRepository, times(1)).save(userCaptor.capture());

		// 캡처된 User 객체 검증
		User savedUser = userCaptor.getValue();

		assertEquals("test username", savedUser.getUsername());
		assertEquals("test nickname", savedUser.getNickname());
		assertEquals("test@example.com", savedUser.getEmail());
		assertEquals(encodedPassword, savedUser.getPassword()); // 암호화된 비밀번호 확인
		assertEquals(Role.NORMAL, savedUser.getRole());
		assertEquals(false, savedUser.getIsDeleted());
		assertEquals(LocalDateTime.now().getDayOfYear(), savedUser.getCreatedAt().getDayOfYear()); // 생성 시간 확인
	}

	@Test // 유저 정보를 찾지 못해 회원정보 업데이트에 실패하는 경우
	void testUpdateUser_UserNotFound() {
		String email = "test@example.com";
		when(userRepository.findByEmail(email)).thenReturn(null);

		assertThrows(NoSuchElementException.class, () -> {
			userService.update(userDto, email);
		});

	}

	@Test //이메일이 달라 회원정보 수정에 실패하는 경우
	void testUpdateUser_EmailMismatch() {
		String email = "test@example.com";
		when(userRepository.findByEmail(email)).thenReturn(existingUser);
		userDto = userDto.builder()
				.email("mismatch@example.com")
				.build();

		assertThrows(CustomException.class, () -> {
			userService.update(userDto, email);
		});

	}

	@Test // 회원정보 수정에 성공하는 경우
	void testUpdateUser_Success() {
		// Given
		String email = "test@example.com";
		String encodedPassword = "encodedPassword";

		// 유저 정보를 업데이트할 DTO
		userDto = UserDto.builder()
				.username("updated username")
				.nickname("updated nickname")
				.email(email)
				.password("newPassword123")
				.solvedacUserName("validSolvedacUser")
				.build();

		// 기존 유저를 반환하도록 설정
		when(userRepository.findByEmail(email)).thenReturn(existingUser);

		// 비밀번호 인코딩 설정
		when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);

		// SolvedAC username 유효성 검사에서 true 반환하도록 설정
		doReturn(true).when(userService).isUsernameValid(userDto.getSolvedacUserName());

		// When
		userService.update(userDto, email);

		// Then
		// ArgumentCaptor를 사용하여 저장된 User 객체를 캡처
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository, times(1)).save(userCaptor.capture());

		// 캡처된 User 객체 검증
		User updatedUser = userCaptor.getValue();

		// 업데이트된 필드 검증
		assertEquals(userDto.getNickname(), updatedUser.getNickname());
		assertEquals(encodedPassword, updatedUser.getPassword()); // 비밀번호 암호화 확인
		assertEquals(userDto.getSolvedacUserName(), updatedUser.getSolvedacUserName()); // SolvedAC 유저명 확인
	}
}