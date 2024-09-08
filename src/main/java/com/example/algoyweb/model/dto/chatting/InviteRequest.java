package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteRequest {

	@NotNull(message = "초대할 사용자 닉네임은 필수입니다.")
	private String nickname;
}