package com.example.algoyweb.exception.errorcode;

import org.springframework.http.HttpStatus;

/**
 * @author JSW
 *
 * 채팅 기능 관련 에러 코드를 정의하는 열거형입니다.
 * 각 에러 코드는 HTTP 상태 코드와 자세한 에러 메시지를 가지고 있습니다.
 */
public enum ChattingErrorCode implements ErrorCode {
	// 채팅방을 찾을 수 없는 경우
	ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
	// 사용자를 찾을 수 없는 경우
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	// 사용자가 채팅방에 참여하지 않은 경우
	USER_NOT_IN_ROOM(HttpStatus.FORBIDDEN, "사용자가 채팅방에 참여하지 않았습니다."),
	// 채팅방 소유자만 초대할 수 있는 경우
	NOT_ROOM_OWNER(HttpStatus.FORBIDDEN, "채팅방 소유자만 초대할 수 있습니다."),
	// 사용자가 이미 채팅방에 참여하고 있는 경우
	USER_ALREADY_IN_ROOM(HttpStatus.BAD_REQUEST, "사용자가 이미 채팅방에 참여하고 있습니다."),
	// 자기 자신을 초대할 수 없는 경우
	SELF_INVITATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 초대할 수 없습니다."),
	// 유효하지 않은 방 이름인 경우
	INVALID_ROOM_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 방 이름입니다."),
	// 초대할 사용자가 없는 경우
	NO_INVITEES(HttpStatus.BAD_REQUEST, "초대할 사용자가 없습니다."),
	// 메시지가 너무 긴 경우
	MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "메시지가 너무 깁니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ChattingErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	/**
	 * 해당 에러 코드에 대한 HTTP 상태 코드를 반환합니다.
	 *
	 * @return HTTP 상태 코드
	 */
	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	/**
	 * 해당 에러 코드에 대한 상세 메시지를 반환합니다.
	 *
	 * @return 에러 메시지
	 */
	@Override
	public String getMessage() {
		return message;
	}
}