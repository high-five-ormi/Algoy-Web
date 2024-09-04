package com.example.algoyweb.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

	/**
	 * 비밀번호 찾기 페이지 보여주기
	 *
	 * @author yuseok
	 * @return 비밀번호 찾기 폼 뷰의 이름 (html)
	 */
	@GetMapping("/find-password")
	public String showFindPasswordPage() {
		return "password/find-password";
	}

	/**
	 * 비밀번호 찾기 요청 처리
	 *
	 * @author yuseok
	 * @param email 입력한 사용자 이메일
	 * @param username 입력한 사용자 이름
	 * @param model 뷰에 전달할 모델 데이터
	 * @return 비밀번호 재설정 페이지로 리다이렉트하거나 오류 메시지를 포함한 비밀번호 찾기 페이지를 반환
	 */
	@PostMapping("/find-password")
	public String findPassword(@RequestParam("email") String email, @RequestParam("username") String username,
		Model model) {
		// 입력한 이메일과 사용자 이름으로 토큰(임시 비밀번호) 발급
		String token = userService.findPassword(email, username);

		// 토큰 생성 시 비밀번호 재설정 페이지로 리다이렉트
		if (token != null) {
			return "redirect:/algoy/set-password?token=" + token;
		}

		// 토큰이 없으면(입력한 유저 정보가 일치하지 않으면) 에러 메시지를 모델에 추가 후 비밀번호 찾기 페이지로 이동
		model.addAttribute("error", "입력한 이메일 또는 사용자 이름이 일치하지 않습니다.");

		return "password/find-password"; // 비밀번호 찾기 페이지로 이동
	}

	/**
	 * 비밀번호 재설정 페이지 보여주기
	 *
	 * @author yuseok
	 * @param token 비밀번호 재설정에 필요한 토큰
	 * @param model 뷰에 전달할 모델 데이터
	 * @return 비밀번호 재설정 페이지
	 */
	@GetMapping("/set-password")
	public String setPasswordPage(@RequestParam("token") String token, Model model) {
		model.addAttribute("token", token); // 모델에 토큰 값 추가

		return "password/reset-password"; // 비밀번호 재설정 페이지로 이동
	}

	/**
	 * 비밀번호 재설정 요청 처리
	 *
	 * @author yuseok
	 * @param token 비밀번호 재설정에 필요한 토큰
	 * @param newPassword 새 비밀번호
	 * @param model 뷰에 전달할 모델 데이터
	 * @return 성공시 로그인 페이지로 리다이렉트, 실패 시 오류 메시지를 포함한 비밀번호 재설정 페이지 반환
	 */
	@PostMapping("/set-password")
	public String setPassword(@RequestParam("token") String token, @RequestParam("confirmPassword") String newPassword,
		Model model) {
		// 토큰을 사용해 비밀번호 재설정
		boolean reset = userService.resetPassword(token, newPassword);

		// 비밀번호 재설정 성공 시 로그인 페이지로 리다이렉트
		if (reset) {
			return "redirect:/algoy/login";
		}

		// 재설정 실패 시 에러 메시지를 모델에 추가 후 비밀번호 재설정 페이지로 이동
		model.addAttribute("error", "토큰이 유효하지 않거나 만료되었습니다.");

		return "password/reset-password"; // 비밀번호 재설정 페이지로 이동
	}
}
