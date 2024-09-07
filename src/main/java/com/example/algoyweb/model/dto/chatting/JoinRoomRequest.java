package com.example.algoyweb.model.dto.chatting;

import lombok.Data;

@Data
public class JoinRoomRequest {
  private String roomId;
  private Long userId;
}