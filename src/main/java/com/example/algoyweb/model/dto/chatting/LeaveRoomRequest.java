package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveRoomRequest {

  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId;
}