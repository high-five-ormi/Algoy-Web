package com.example.algoyweb.model.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author JSW
 *
 * 채팅방 참여 성공 응답을 나타내는 DTO 클래스입니다.
 * 채팅방에 참여한 후 클라이언트에게 반환됩니다.
 */
@Getter
@AllArgsConstructor
public class JoinRoomResponse {

  // 참여한 채팅방의 이름
  private String roomName;

  // 참여한 채팅방에 대한 정보
  private ChattingRoomDto roomDto;
}