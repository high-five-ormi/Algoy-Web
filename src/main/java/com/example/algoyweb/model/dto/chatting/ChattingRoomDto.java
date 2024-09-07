package com.example.algoyweb.model.dto.chatting;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChattingRoomDto {

  private Long id;
  private String roomId;
  private String name;
  private List<Long> participants;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}