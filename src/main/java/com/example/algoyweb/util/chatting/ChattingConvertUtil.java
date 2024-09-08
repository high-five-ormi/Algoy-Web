package com.example.algoyweb.util.chatting;

import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import com.example.algoyweb.model.entity.user.User;
import java.time.LocalDateTime;

public class ChattingConvertUtil {
  public static Chatting convertToEntity(ChattingDto chattingDto, User user) {
    return Chatting.builder()
        .user(user)
        .roomId(chattingDto.getRoomId())
        .content(chattingDto.getContent())
        .createdAt(LocalDateTime.now())
        .build();
  }

  public static ChattingDto convertToDto(Chatting chatting) {
    return ChattingDto.builder()
        .userId(chatting.getUser().getUserId())
        .roomId(chatting.getRoomId())
        .content(chatting.getContent())
        .nickname(chatting.getNickname())
        .build();
  }

  public static ChattingRoom convertToEntity(ChattingRoomDto chattingRoomDto) {
    return ChattingRoom.builder()
        .roomId(chattingRoomDto.getRoomId())
        .name(chattingRoomDto.getName())
        .participants(chattingRoomDto.getParticipants())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

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