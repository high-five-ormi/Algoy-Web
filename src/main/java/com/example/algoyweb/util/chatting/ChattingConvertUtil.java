package com.example.algoyweb.util.chatting;

import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import com.example.algoyweb.model.entity.user.User;
import java.time.LocalDateTime;

/**
 * @author JSW
 *
 * DTO와 엔티티 간의 변환 메서드를 제공합니다.
 */
public class ChattingConvertUtil {

  /**
   * ChattingDto 객체를 Chatting 엔티티로 변환합니다.
   *
   * @param chattingDto 변환할 ChattingDto 객체
   * @param user 채팅을 작성한 User 객체
   * @return 변환된 Chatting 엔티티
   */
  public static Chatting convertToEntity(ChattingDto chattingDto, User user) {
    return Chatting.builder()
        .user(user)
        .roomId(chattingDto.getRoomId())
        .content(chattingDto.getContent())
        .nickname(chattingDto.getNickname())
        .createdAt(chattingDto.getCreatedAt() != null ? chattingDto.getCreatedAt() : LocalDateTime.now())
        .build();
  }

  /**
   * Chatting 엔티티를 ChattingDto 객체로 변환합니다.
   *
   * @param chatting 변환할 Chatting 엔티티
   * @return 변환된 ChattingDto 객체
   */
  public static ChattingDto convertToDto(Chatting chatting) {
    return ChattingDto.builder()
        .userId(chatting.getUser().getUserId())
        .roomId(chatting.getRoomId())
        .content(chatting.getContent())
        .nickname(chatting.getNickname())
        .createdAt(chatting.getCreatedAt() != null ? chatting.getCreatedAt() : LocalDateTime.now())
        .build();
  }

  /**
   * ChattingRoomDto 객체를 ChattingRoom 엔티티로 변환합니다.
   *
   * @param chattingRoomDto 변환할 ChattingRoomDto 객체
   * @return 변환된 ChattingRoom 엔티티
   */
  public static ChattingRoom convertToEntity(ChattingRoomDto chattingRoomDto) {
    return ChattingRoom.builder()
        .roomId(chattingRoomDto.getRoomId())
        .name(chattingRoomDto.getName())
        .participants(chattingRoomDto.getParticipants())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  /**
   * ChattingRoom 엔티티를 ChattingRoomDto 객체로 변환합니다.
   *
   * @param chattingRoom 변환할 ChattingRoom 엔티티
   * @return 변환된 ChattingRoomDto 객체
   */
  public static ChattingRoomDto convertToDto(ChattingRoom chattingRoom) {
    return ChattingRoomDto.builder()
        .id(chattingRoom.getId())
        .roomId(chattingRoom.getRoomId())
        .name(chattingRoom.getName())
        .participants(chattingRoom.getParticipants())
        .createdAt(chattingRoom.getCreatedAt())
        .updatedAt(chattingRoom.getUpdatedAt())
        .build();
  }
}