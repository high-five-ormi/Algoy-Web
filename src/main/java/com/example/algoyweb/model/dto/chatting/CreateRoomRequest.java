package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoomRequest {

  @NotNull(message = "채팅방 이름은 필수입니다.")
  @Size(max = 50, message = "채팅방 이름은 50자를 초과할 수 없습니다.")
  private String name;
}