package com.example.algoyweb.model.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChattingDto {

  private Long userId;
  private String roomId;
  private String content;
}