package com.example.algoyweb.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.algoyweb.service.user.UserService;

@Controller
@RequestMapping("/algoy")
public class AdminController {
	private final UserService userService;

	@Autowired
	public AdminController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 관리자 페이지 보여주기
	 *
	 * @author yuseok
	 * @param model 뷰에 데이터를 전달하기 위한 Model 객체 (모든 유저 리스트)
	 * @return 관리자 페이지 폼 뷰 이름 (html)
	 */
	@GetMapping("/admin")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')") // 관리자만 접근 허용
	public String showAdminPage(Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("users", userService.getAllUsers());

		return "user/admin";
	}

	/**
	 * 사용자의 역할(Role) 변경: 관리자가 특정 사용자의 역할을 변경할 때 사용
	 *
	 * @param userId 역할을 변경할 사용자의 ID
	 * @param action 수행할 작업. 가능한 값: "admin" (관리자 승격), "ban" (사용자 밴), "lift" (밴 해제)
	 * @param banReason 유저의 밴 사유 (필수값 아님)
	 * @return 관리자 페이지로 리다이렉트할 URL
	 */
	@PostMapping("/admin/role-control")
	public String changeUserRole(@RequestParam("userId") Long userId, @RequestParam("action") String action,
		@RequestParam(value = "banReason", required = false) String banReason) {
		// 유저 권한 변경 로직
		if ("admin".equals(action)) { // 관리자 승격
			userService.promoteToAdmin(userId);
		} else if ("ban".equals(action)) { // 유저 밴
			userService.banUser(userId, banReason);
		} else if ("lift".equals(action)) { // 유저 밴 해제
			userService.liftBan(userId);
		}

		return "redirect:/algoy/admin";
	}
}