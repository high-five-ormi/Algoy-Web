package com.example.algoyweb.controller.user;

import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.allen.AllenService;
import com.example.algoyweb.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService; //로그인 정보 가져오기
    private final AllenService allenService; //앨런에게 답변가지오기

    public UserAuthenticationSuccessHandler(UserService userService, AllenService allenService) {
        this.userService = userService;
        this.allenService = allenService;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException{
        System.out.println("===============component check====================");
        // 로그인한 사용자의 정보를 가져옵니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // UserService를 통해 유저 정보를 가져옵니다.
        User user = userService.findByEmail(username);  // 혹은 findByUsername 메서드를 사용할 수도 있습니다.

        // solvedAC 유저네임이 null인지 확인하고 로직을 추가합니다.
        if (user.getSolvedacUserName() == null) {
            // solvedAC 유저네임이 없으면 특정 메시지를 세션에 저장하거나 다른 처리
            request.getSession().setAttribute("showSolvedAcMessage", true);
        } else {
            // solvedAC 유저네임이 있으면 문제 추천 로직 실행
            try {
                allenService.sovledacCall(user.getUsername(),user.getSolvedacUserName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 홈 페이지로 리다이렉트
        response.sendRedirect("/algoy/home");

    }
}
