package com.example.algoyweb.util.user;

import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.allen.AllenService;
import com.example.algoyweb.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final AllenService allenService;

    public UserAuthenticationSuccessHandler(UserService userService, AllenService allenService) {
        this.userService = userService;
        this.allenService = allenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // UserService를 통해 사용자 정보 가져오기
        User user = userService.findByEmail(username);

        // SolvedAC 유저네임이 null인지 확인하고 로직 추가
        if (user.getSolvedacUserName() == null) {
            // SolvedAC 유저네임이 없으면 세션에 특정 메시지 저장
            request.getSession().setAttribute("showSolvedAcMessage", true);
        } else {
            // SolvedAC 유저네임이 있으면 문제 추천 로직 실행
            try {
                allenService.sovledacCall(user.getUsername(), user.getSolvedacUserName());
            } catch (Exception e) {
                // 예외를 잡아서 로그를 남기고, 사용자에게 적절한 피드백을 제공
                e.printStackTrace(); // 콘솔에 에러 출력
                request.getSession().setAttribute("solvedacErrorMessage", "Problem recommendation failed");
            }
        }

        // 사용자 삭제 여부 확인 후 리다이렉트 URL 결정
        String redirectUrl = user.getIsDeleted() ? "/algoy/user/restore" : "/algoy/home";
        response.sendRedirect(redirectUrl);
    }
}
