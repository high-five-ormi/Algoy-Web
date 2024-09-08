package com.example.algoyweb.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    COMMENT_PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 코멘트를 찾지 못한 경우"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "코멘트 ID에 맞는 코멘트를 찾지 못한 경우"),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 사용자가 접근한 경우");
    private final HttpStatus httpStatus;
    private final String message;
}
