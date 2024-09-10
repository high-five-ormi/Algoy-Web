package com.example.algoyweb.model.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author JSW
 *
 * 에러 응답을 나타내는 DTO 클래스입니다.
 * API 요청이 실패했을 때 클라이언트에게 에러 메시지를 전달하는 데 사용됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	// 에러 메시지
	private String message;
}