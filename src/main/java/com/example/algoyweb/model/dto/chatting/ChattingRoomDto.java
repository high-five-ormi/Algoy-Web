package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  @NotNull(message = "채팅방 이름은 필수입니다.")
  @Size(max = 50, message = "채팅방 이름은 50자를 초과할 수 없습니다.")
  private String name;

  private List<Long> participants;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}