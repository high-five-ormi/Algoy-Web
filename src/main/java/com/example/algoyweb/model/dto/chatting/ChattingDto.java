package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * @author JSW
 *
 * 채팅 메시지에 대한 정보를 담는 DTO 클래스입니다.
 */
@Getter
@Builder
public class ChattingDto {

  // 메시지를 보낸 사용자의 ID
  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long userId;

  // 메시지를 보낸 채팅방의 ID
  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  // 메시지 내용
  @NotNull(message = "메시지 내용은 필수입니다.")
  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다.")
  private String content;

  // 메시지를 보낸 사용자의 닉네임
  private String nickname;

  // 메시지 생성 시간
  private LocalDateTime createdAt;
}