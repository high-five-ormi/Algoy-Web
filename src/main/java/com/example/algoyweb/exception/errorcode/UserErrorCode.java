package com.example.algoyweb.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

    USER_NOT_EQUAL_EMAIL(HttpStatus.BAD_REQUEST, "유저 로그인 이메일 불일치"),
    USER_NOT_DELETED(HttpStatus.MULTI_STATUS, "삭제 예정인 유저가 아닐 경우"),
    USER_NOT_EQUAL_ID(HttpStatus.FORBIDDEN, "작업을 요청한 유저가 권한이 없는 경우"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않는 경우");

    private final HttpStatus httpStatus;
    private final String message;
}
