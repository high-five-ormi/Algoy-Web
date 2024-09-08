package com.example.algoyweb.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

    USER_NOT_EQUAL_EMAIL(HttpStatus.BAD_REQUEST, "유저 로그인 이메일 불일치"),
    USER_NOT_DELETED(HttpStatus.MULTI_STATUS, "삭제 예정인 유저가 아닐 경우"),
    INVALID_SOLVEDAC_USERNAME(HttpStatus.BAD_REQUEST, "유효하지 않은 Solved.ac 사용자 이름");

    private final HttpStatus httpStatus;
    private final String message;
}
