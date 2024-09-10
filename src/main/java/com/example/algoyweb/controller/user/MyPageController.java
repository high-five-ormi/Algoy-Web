package com.example.algoyweb.controller.user;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
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

    /**
     * 닉네임 반환
     *
     * @author jooyoung
     * @param userDetails 이메일로 유저의 닉네임을 찾아 반환함.
     * @return userNickname
     * 다른 코드랑 합칠 수 있을 것 같아요 (지금은 모달에 띄우기 위해 사용합니다)
     */

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


    /**
     * 유저 수정
     *
     * @author 창섭님,jooyoung
     * @param userDetails 로그인 된 유저 수정폼을 불러옴
     * @return mav, 수정 완료
     */
    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView editForm(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        ModelAndView mav = new ModelAndView("mypage/userEdit");
        mav.addObject("user", user);

        return mav;
    }


    // 사용자 정보 수정
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Received solvedac username: " + userDto.getSolvedacUserName());
        String email = userDetails.getUsername();
        try {
            userService.update(userDto, email);
            return ResponseEntity.status(HttpStatus.OK).body("User information updated successfully.");
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    /**
     * 유저 삭제
     *
     * @author 창섭님,jooyoung
     * @param userDetails 로그인 된 유저를 소프트딜리트함
     * @return mav, 삭제완료 메세지
     */
    @GetMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ModelAndView deleteForm(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        ModelAndView mav = new ModelAndView("mypage/userDelete");
        mav.addObject("user", user);

        return mav;
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_NORMAL', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        // 사용자 삭제 예약 로직
        userService.setDeleted(userDetails.getUsername(), request, response);

        // 성공 응답 반환
        return ResponseEntity.ok("회원 탈퇴 요청이 완료되었습니다.");
    }



    /**
     * 유저 복구
     *
     * @author jooyoung
     * @param userDetails,model 탈퇴 유저를 복구함
     * @return 복구 여부를 묻는 페이지, 복구 완료 시 홈 화면
     */
    @GetMapping("/restore")
    public String restoreAccountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = user.getDeletedAt().format(formatter);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("user", user);
        return "mypage/userRestore";
    }

    @PostMapping("/restore")
    public String restoreAccount(@AuthenticationPrincipal UserDetails userDetails) {
        userService.restoreAccount(userDetails.getUsername());
        return "redirect:/algoy/home";
    }
}