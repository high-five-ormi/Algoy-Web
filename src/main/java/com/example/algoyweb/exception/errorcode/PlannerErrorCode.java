package com.example.algoyweb.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerErrorCode implements ErrorCode {

    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 플랜입니다."),
    PLAN_NOT_EQUAL_ID(HttpStatus.UNAUTHORIZED, "플랜의 작성자가 다른 경우")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
