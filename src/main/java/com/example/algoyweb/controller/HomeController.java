package com.example.algoyweb.controller;

import com.example.algoyweb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 화면 컨트롤러
 * 로그인 성공 이후 화면을 위해 작성
 * @author jooyoung
 */

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    // ai-backend.url 설정값을 저장하는 변수입니다.
    @Value("${ai-backend.url}")
    private String backendUrl;

    @GetMapping("/algoy/home")
    public String home(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "home"; // view name
    }
}