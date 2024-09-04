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
        boolean isAuthenticated = userService.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "home"; // view name
    }
}
