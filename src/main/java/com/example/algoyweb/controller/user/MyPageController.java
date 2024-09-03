package com.example.algoyweb.controller.user;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/algoy/user")
public class MyPageController {

    private final UserService userService;

    public MyPageController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 닉네임을 반환하는 API
    @GetMapping("/nickname")
    public ResponseEntity<Map<String, String>> getUserNickname(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "User is not authenticated"));
        }

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        if (user != null) {
            Map<String, String> response = new HashMap<>();
            response.put("nickname", user.getNickname());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }
    }

    // 유저 수정 폼 불러오기
    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView editForm(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        boolean isAuthenticated = userService.isAuthenticated();
        ModelAndView mav = new ModelAndView("mypage/userEdit");
        mav.addObject("user", user);
        mav.addObject("isAuthenticated", isAuthenticated);
        return mav;
    }

    // 사용자 정보 수정
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        try {
            userService.update(userDto, email);
            return ResponseEntity.status(HttpStatus.OK).body("User information updated successfully.");
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // 유저 수정 폼 불러오기
    @GetMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView deleteForm(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        boolean isAuthenticated = userService.isAuthenticated();
        ModelAndView mav = new ModelAndView("mypage/userDelete");
        mav.addObject("user", user);
        mav.addObject("isAuthenticated", isAuthenticated);
        return mav;
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteRequest(@AuthenticationPrincipal UserDetails userDetails) {
        userService.setDeleted(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("삭제 요청이 완료되었습니다.");
    }
}