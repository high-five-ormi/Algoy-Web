package com.example.algoyweb.exception.chatting;

import com.example.algoyweb.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ChattingErrorCode implements ErrorCode {
	ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	USER_NOT_IN_ROOM(HttpStatus.FORBIDDEN, "사용자가 채팅방에 참여하지 않았습니다."),
	NOT_ROOM_OWNER(HttpStatus.FORBIDDEN, "채팅방 소유자만 초대할 수 있습니다."),
	USER_BANNED(HttpStatus.FORBIDDEN, "정지된 사용자는 초대할 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ChattingErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}
}