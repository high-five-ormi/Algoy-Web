package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * @author JSW
 *
 * 채팅 메시지를 전송하기 위한 요청 정보를 담는 DTO 클래스입니다.
 * 채팅 메시지 전송 API에서 사용됩니다.
 */
@Getter
public class MessageRequest {

  // 메시지를 전송할 채팅방의 고유 식별자
  @NotNull(message = "채팅방 ID는 필수입니다.")
  private String roomId;

  // 전송할 메시지 내용
  @NotNull(message = "메시지 내용은 필수입니다.")
  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다.")
  private String content;
}