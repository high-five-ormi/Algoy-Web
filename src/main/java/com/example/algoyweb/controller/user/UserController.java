package com.example.algoyweb.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.user.UserService;

@Controller
@RequestMapping("/algoy")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 로그인 폼 보여주기
   *
   * @author jooyoung
   * @param model 뷰에 데이터를 전달하기 위한 Model 객체 (근데 여기서 model이 꼭 필요한지 모르겠어요)
   * @return 로그인 폼 뷰의 이름 (html)
   */
  @GetMapping("/login")
  public String showLoginForm(Model model) {
    // 새로운 UserDto 객체를 생성 후, "userDto"라는 이름으로 Model에 추가
    model.addAttribute("user", new User());

    return "user/login";
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
    // 새로운 User 객체를 생성 후, "user"라는 이름으로 Model에 추가
    model.addAttribute("user", new User());

    return "signup/signup";
  }

  /**
   * 회원가입 요청 처리
   *
   * @author yuseok
   * @param userDto 회원가입을 위해 사용자가 입력한 정보 DTO
   * @return 회원가입 성공 후 리다이렉트할 URL
   */
  @PostMapping("/sign")
  public String signUp(@ModelAttribute("user") UserDto userDto) {
    userService.signUpUser(userDto);

    return "redirect:/algoy/login"; // 회원가입 성공시 로그인 화면으로 리다이렉트
  }

  /**
   * 이메일 중복 확인
   *
   * @author yuseok
   * @param email 중복 확인할 이메일 주소
   * @return 이메일 중복 여부를 나타내는 ResponseEntity
   */
  @GetMapping("/check-email-duplicate")
  public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(@RequestParam("email") String email) {
    boolean exists = userService.findByEmail(email) != null;

    Map<String, Boolean> response = new HashMap<>();

    response.put("exists", exists);

    return ResponseEntity.ok(response);
  }

  /**
   * 닉네임 중복 확인
   *
   * @author yuseok
   * @param nickname 중복 확인할 닉네임
   * @return 닉네임 중복 여부를 나타내는 ResponseEntity
   */
  @GetMapping("/check-nickname-duplicate")
  public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
    boolean exists = userService.findByNickname(nickname) != null;

    Map<String, Boolean> response = new HashMap<>();

    response.put("exists", exists);

    return ResponseEntity.ok(response);
  }


  // 유저 수정
  @GetMapping("/user/edit")
  @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
  public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @AuthenticationPrincipal UserDetails userDetails) {
    UserDto updatedUser = userService.update(userDto, userDetails.getUsername());

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  // 유저 삭제(요청)
  @PostMapping("/user/delete")
  @PostAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
  public ResponseEntity<String> deleteRequest (@AuthenticationPrincipal UserDetails userDetails) {
    userService.setDeleted(userDetails.getUsername());

    return ResponseEntity.status(HttpStatus.OK).body("삭제 요청이 완료되었습니다.");
  }
}
