package com.example.algoyweb.model.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaveRoomResponse {
	private String roomName;
	private String message;
	private ChattingRoomDto roomDto;
}