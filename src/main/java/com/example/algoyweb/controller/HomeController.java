package com.example.algoyweb.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/algoy/home")
    public String home(Model model) {
        //로그인 여부 확인 -> 로그인 / 로그아웃 구분을 위해 사용합니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "home"; // view name
    }
}
