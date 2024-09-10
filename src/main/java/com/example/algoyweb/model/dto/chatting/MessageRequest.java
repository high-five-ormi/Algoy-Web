package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MessageRequest {

  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  @NotNull(message = "메시지 내용은 필수입니다.")
  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다.")
  private String content;
}