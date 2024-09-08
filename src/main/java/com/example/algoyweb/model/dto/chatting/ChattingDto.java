package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChattingDto {

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId;

  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  @NotNull(message = "메시지 내용은 필수입니다.")
  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다.")
  private String content;
}