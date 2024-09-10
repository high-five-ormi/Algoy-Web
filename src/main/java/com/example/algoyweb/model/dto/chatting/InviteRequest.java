package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author JSW
 *
 * 다른 사용자를 채팅방에 초대하기 위한 요청 정보를 담는 DTO 클래스입니다.
 * 채팅방에 사용자를 초대하는 API에서 사용됩니다.
 */
@Data
public class InviteRequest {

	// 채팅방에 초대할 사용자의 닉네임
	@NotNull(message = "초대할 사용자 닉네임은 필수입니다.")
	private String nickname;
}