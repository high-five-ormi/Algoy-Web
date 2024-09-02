package com.example.algoyweb.controller.user;

import static com.example.algoyweb.model.entity.user.Role.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.service.user.UserService;

@WebMvcTest(value = UserController.class) // UserController를 테스트하기 위한 설정
public class UserControllerTest {
  @Autowired private MockMvc mockMvc; // MockMvc 객체 주입

  @MockBean private UserService userService; // UserService를 Mock 객체로 주입

  private UserDto signDto; // 테스트에 사용할 UserDto 객체

  @BeforeEach
  public void setUp() { // 테스트 전에 실행되는 설정 메서드
    signDto = UserDto.builder() // UserDto 빌더 패턴으로 객체 생성
		.username("test username")
		.nickname("test nickname")
		.email("test@example.com")
		.password("password123")
		.role(NORMAL)
		.isDeleted(false)
		.build();
  }

  @Test
  @WithMockUser // 인증된 사용자로 요청 시뮬레이션 (403 해결)
  void shouldSignUpUser() throws Exception {
    // 동작
    mockMvc.perform(MockMvcRequestBuilders.post("/algoy/sign") // "/algoy/sign" 경로로 POST 요청
		.contentType(MediaType.APPLICATION_FORM_URLENCODED) // 요청의 콘텐츠 타입 설정
		.with(csrf()) // CSRF 토큰 포함 (403 해결)
		.flashAttr("user", signDto)) // flash attribute로 signDto 포함
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // 3xx 리다이렉션 상태 예상
        .andExpect(MockMvcResultMatchers.redirectedUrl("/algoy/login")); // "/algoy/login"으로 리다이렉션 예상

    verify(userService).signUpUser(signDto); // userService의 signUpUser 메서드가 호출되었는지 검증
  }
}