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
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService, AllenService allenService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // UserService를 통해 사용자 상태 확인
        User user = userService.findByEmail(username);
        String redirectUrl = user.getIsDeleted() ? "/algoy/user/restore" : "/algoy/home";
        response.sendRedirect(redirectUrl);
    }
}
