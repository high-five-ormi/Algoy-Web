package com.example.algoyweb.exception;

import com.example.algoyweb.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


// 커스텀 익셉션
@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
