package com.example.algoyweb.service.user;

import static com.example.algoyweb.model.entity.user.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

	@InjectMocks
	private UserService userService;

	private UserDto userDto;

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
}