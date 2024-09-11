package com.example.algoyweb.controller;

import com.example.algoyweb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    // ai-backend.url 설정값을 저장하는 변수입니다.
    @Value("${ai-backend.url}")
    private String backendUrl;


    /**
     * 홈 화면에서 추천 문제를 표시
     *
     * @author 조아라
     * @param userDetails 인증 정보를 포함한 Authentication 객체
     * @param model 뷰에 데이터를 전달하기 위한 Model 객체
     * @return 홈 화면 뷰의 이름 (html)
     */
    @GetMapping("/algoy/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        // 사용자가 인증되지 않았다면 로그인 페이지로 리다이렉트
        if (userDetails == null) {
            model.addAttribute("problem", null);
            model.addAttribute("backendUrl", backendUrl);
            return "home";
        }
        System.out.println("유저 체크");
        // 현재 로그인한 사용자 이름 가져오기
        String userEamil = userDetails.getUsername();
        System.out.println("getEmail? " + userEamil);

        // 로그인한 사용자의 solvedACUserName 가져오기
        Boolean CheckedSolvedACUserName = userService.checkSolvedACUserNameByUsername(userEamil);

        // solvedACUserName이 null인 경우 홈 화면 유지
        if (!CheckedSolvedACUserName) {
            model.addAttribute("problem", null);
            model.addAttribute("backendUrl", backendUrl);
            return "home"; // SolvedAC username이 없는 경우, 홈 화면에 머무름
        }
        System.out.println("solvedAC username 존재함");
        // 로그인한 사용자 solvedAC username 기반 추천 문제 가져오기
        String problemToShow = userService.getRandomProblemsByUsername(userEamil);
        System.out.println("=============" +problemToShow +"=================");

        // 추천 문제가 존재할 경우에만 전달
        model.addAttribute("problem", problemToShow);
        model.addAttribute("backendUrl", backendUrl);
        return "home"; // view name
    }
}