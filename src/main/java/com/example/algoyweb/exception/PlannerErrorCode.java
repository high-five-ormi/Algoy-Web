package com.example.algoyweb.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerErrorCode implements ErrorCode {

    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 플랜입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
