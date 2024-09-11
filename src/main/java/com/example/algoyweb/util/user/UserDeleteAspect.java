package com.example.algoyweb.util.user;

import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class UserDeleteAspect {

    private final UserService userService;

    // 서비스 메서드 중 특정 메서드들을 제외한 모든 메서드에 포인트컷을 설정
    @Pointcut("execution(* com.example.algoyweb.service.*(..)) " +
            "&& !execution(* com.example.algoyweb.service.user.UserService.delete(..))" +
            "&& !execution(* com.example.algoyweb.service.user.UserService.findAll(..))")
    public void serviceMethods() {}

    // 포인트컷에 해당하는 메서드가 실행된 후에 실행되는 어드바이스
    @After("serviceMethods()")
    @Transactional
    public void checkDeleted() {
        // 모든 사용자 목록을 조회
        List<User> userList = userService.findAll();

        // 사용자 목록을 순회하며 삭제된 사용자 여부를 확인
        for (User user : userList) {
            // 사용자가 '삭제됨' 상태인지 확인
            if (user.getIsDeleted()) {
                // 현재 시간이 사용자의 삭제 예약 시간 이후인지 확인
                if (LocalDateTime.now().isAfter(user.getDeletedAt())) {
                    // 삭제 예약 시간이 지난 사용자 계정을 실제로 삭제
                    userService.delete(user.getUsername());
                }
            }
        }
    }
}