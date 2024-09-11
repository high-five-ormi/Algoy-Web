package com.example.algoyweb.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum StudyErrorCode implements ErrorCode {

    STUDY_NOT_FOUND(HttpStatus.BAD_REQUEST, "잘못된 스터디 ID"),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "스터디 참여자를 찾지 못한 경우");

    private final HttpStatus httpStatus;
    private final String message;
}
