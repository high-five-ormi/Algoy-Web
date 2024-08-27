package com.example.algoyweb.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.user.UserService;

@Controller
@RequestMapping("/algoy")
public class UserController {
  UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }
  @GetMapping("/login")
  public String showLoginForm(Model model) {
    // 새로운 UserDto 객체를 생성 후, "userDto"라는 이름으로 Model에 추가
    model.addAttribute("user", new User());

    return "login";
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
  public String signUp(@ModelAttribute("user") UserDto userDto, Model model) {
    User user = userService.findByNickname(userDto.getNickname());

    if (user != null) { // 닉네임 중복 시
      model.addAttribute("nicknameExist", user);
      return "signup/signup";
    }

    User emailuser = userService.findByEmail(userDto.getEmail());

    if (emailuser != null) { // 이메일 중복 시
      model.addAttribute("emailExist", emailuser);
      return "signup/signup";
    }

    userService.signUpUser(userDto);

    return "redirect:/login"; // 회원가입 성공시 로그인 화면으로
  }
}
