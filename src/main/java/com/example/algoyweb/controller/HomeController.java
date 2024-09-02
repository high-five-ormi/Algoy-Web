package com.example.algoyweb.controller;

import com.example.algoyweb.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 화면 컨트롤러
 * 로그인 성공 이후 화면을 위해 작성
 * @author jooyoung
 */

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/algoy/home")
    public String home(Model model) {
        // 로그인 여부 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        // 로그인된 사용자의 닉네임 가져오기
        String userNickname = "로그인하세요!"; // 기본값

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String email = userDetails.getUsername(); // 사용자 이메일
                userNickname = userService.getUserNicknameByEmail(email); // 사용자 닉네임
            }
        }

        // 모델에 데이터 추가
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("userNickname", userNickname);

        return "home"; // view name
    }
}
