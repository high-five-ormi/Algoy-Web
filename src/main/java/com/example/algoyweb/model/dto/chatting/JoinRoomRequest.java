package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author JSW
 *
 * 기존 채팅방에 참여하기 위한 요청 정보를 담는 DTO 클래스입니다.
 * 채팅방 참여 API에서 사용됩니다.
 */
@Data
public class JoinRoomRequest {

  // 참여할 채팅방의 고유 식별자
  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  // 채팅방에 참여할 사용자의 이름
  @NotNull(message = "사용자 이름은 필수입니다.")
  private String username;
}