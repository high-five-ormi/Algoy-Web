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

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @After("serviceMethods()")
    @Transactional
    public void checkDeleted() {
        List<User> userList = userService.findAll();
        for(User user : userList) {
            if(user.getIsDeleted()) {
                if(LocalDateTime.now().isAfter(user.getDeletedAt())) {
                    userService.delete(user.getUsername());
                }
            }
        }
    }
}
