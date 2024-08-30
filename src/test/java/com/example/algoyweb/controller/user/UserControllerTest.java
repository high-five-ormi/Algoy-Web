package com.example.algoyweb.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
  @Autowired private MockMvc mockMvc;

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @Test
  void testSignUp_Success() throws Exception {
    UserDto userDto = UserDto.builder().password("password").build();

    when(userService.signUpUser(any(UserDto.class))).thenReturn(userDto);

    mockMvc
        .perform(
            post("/algoy/sign").param("confirmPassword", "password").flashAttr("user", userDto))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/algoy/login"));
  }

  @Test
  void testSignUp_PasswordMismatch() throws Exception {
    // given
    UserDto userDto = UserDto.builder().password("password").build();

    // when
    // No need to mock UserService here since it should not be called

    // then
    mockMvc
        .perform(
            post("/algoy/sign")
                .param("confirmPassword", "differentPassword")
                .flashAttr("user", userDto))
        .andExpect(status().isOk())
        .andExpect(view().name("signup/signup"))
        .andExpect(model().attribute("passwordMismatch", true));
  }

  @Test
  void testCheckEmailDuplicate_Exists() throws Exception {
    // given
    String email = "test@example.com";

    // when
    when(userService.findByEmail(anyString())).thenReturn(UserDto.builder().email(email).build().toEntity());

    // then
    mockMvc
        .perform(get("/algoy/check-email-duplicate").param("email", email))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.exists").value(true));
  }

  @Test
  void testCheckEmailDuplicate_NotExists() throws Exception {
    // given
    String email = "test@example.com";

    // when
    when(userService.findByEmail(anyString())).thenReturn(null);

    // then
    mockMvc
        .perform(get("/algoy/check-email-duplicate").param("email", email))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.exists").value(false));
  }
}
