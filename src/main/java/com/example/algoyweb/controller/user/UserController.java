package com.example.algoyweb.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.algoyweb.model.dto.auth.SessionUser;
import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/algoy")
public class UserController {
  UserService userService;
  private final HttpSession httpSession;

  @GetMapping("/login")
  public String showLoginForm(Model model) {
    // 새로운 UserDto 객체를 생성 후, "userDto"라는 이름으로 Model에 추가
    model.addAttribute("user", new User());

    return "login";
  }

  @GetMapping("/login-google")
  public String googleLogin(Model model) {
    // 세션에서 사용자 정보 꺼내기
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");

    if (sessionUser != null) { // 로그인된 유저가 있으면
      model.addAttribute("userName", sessionUser.getUsername());
    }

    return "google-login/google-login";
  }

  /**
   * 회원가입 폼 보여주기
   *
   * @author yuseok
   * @param model 뷰에 데이터를 전달하기 위한 Model 객체
   * @return 회원가입 폼 뷰의 이름 (html)
   */
  @GetMapping("/sign")
  public String showSignUpForm(Model model) {
    // 새로운 UserDto 객체를 생성 후, "userDto"라는 이름으로 Model에 추가
    model.addAttribute("user", new User());

    return "signup/signup";
  }

  /**
   * 회원가입 요청 처리
   *
   * @author yuseok
   * @param userDto 회원가입을 위해 사용자가 입력한 정보 DTO, model 뷰에 데이터를 전달하기 위한 Model 객체
   * @return 회원가입 성공 후 리다이렉트할 URL
   */
  @PostMapping("/sign")
  public String signUp(
      @ModelAttribute("user") UserDto userDto,
      @RequestParam("confirmPassword") String confirmPassword,
      Model model) {
    if (!userDto.getPassword().equals(confirmPassword)) { // 비밀번호 확인 시 다른 비밀번호가 입력되면
      model.addAttribute("passwordMismatch", true);

      return "signup/signup";
    }

    userService.signUpUser(userDto);

    return "redirect:/algoy/login"; // 회원가입 성공시 로그인 화면으로 리다이렉트
  }

  @GetMapping("/check-email-duplicate")
  public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(
      @RequestParam("email") String email) {
    boolean exists = userService.findByEmail(email) != null;

    Map<String, Boolean> response = new HashMap<>();

    response.put("exists", exists);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/check-nickname-duplicate")
  public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(
      @RequestParam("nickname") String nickname) {
    boolean exists = userService.findByNickname(nickname) != null;

    Map<String, Boolean> response = new HashMap<>();

    response.put("exists", exists);

    return ResponseEntity.ok(response);
  }
}
