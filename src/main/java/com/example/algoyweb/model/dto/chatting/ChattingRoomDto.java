package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author (작성자 이름)
 *
 * 채팅방에 대한 정보를 담고 있는 DTO 클래스입니다.
 * 채팅방 생성, 조회, 수정 등 다양한 채팅 기능에서 사용됩니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChattingRoomDto {

  // 채팅방의 고유 식별자
  private Long id;

  // 채팅방의 고유한 ID
  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  // 채팅방의 이름
  @NotNull(message = "채팅방 이름은 필수입니다.")
  @Size(max = 50, message = "채팅방 이름은 50자를 초과할 수 없습니다.")
  private String name;

  // 채팅방 생성자의 ID
  private Long ownerId;

  // 채팅방에 참여하고 있는 사용자들의 ID 리스트
  private List<Long> participants;

  // 채팅방이 생성된 시간
  private LocalDateTime createdAt;

  // 채팅방 정보가 마지막으로 수정된 시간
  private LocalDateTime updatedAt;

  // 채팅방 삭제 여부 (true: 삭제됨, false: 활성화)
  private boolean deleted;
}