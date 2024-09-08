package com.example.algoyweb.service.chatting;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.chatting.ChattingErrorCode;
import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.chatting.ChattingRepository;
import com.example.algoyweb.repository.chatting.ChattingRoomRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.chatting.ChattingConvertUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableAsync
@Transactional
@RequiredArgsConstructor
public class ChattingService {

  private final ChattingRepository chattingRepository;
  private final ChattingRoomRepository chattingRoomRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public Page<ChattingDto> getRoomMessages(String roomId, Pageable pageable) {
    Page<Chatting> messages = chattingRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
    return messages.map(ChattingConvertUtil::convertToDto);
  }

  public ChattingRoomDto createRoom(String roomName) {
    ChattingRoom chattingRoom =
        ChattingRoom.builder()
            .roomId("room-" + System.currentTimeMillis())
            .name(roomName)
            .participants(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    ChattingRoom savedRoom = chattingRoomRepository.save(chattingRoom);
    return ChattingConvertUtil.convertToDto(savedRoom);
  }

  public void joinRoom(String roomId, Long userId) {
    ChattingRoom room =
        chattingRoomRepository
            .findByRoomId(roomId)
            .orElseThrow(() -> new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));
    if (!room.getParticipants().contains(userId)) {
      room.addParticipant(userId);
      chattingRoomRepository.save(room);
    }
  }

  public void leaveRoom(String roomId, Long userId) {
    ChattingRoom room =
        chattingRoomRepository
            .findByRoomId(roomId)
            .orElseThrow(() -> new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));
    room.removeParticipant(userId);
    chattingRoomRepository.save(room);
  }

  @Async
  public void saveMessageAsync(ChattingDto chattingDto) {
    User user =
        userRepository
            .findById(chattingDto.getUserId())
            .orElseThrow(() -> new CustomException(ChattingErrorCode.USER_NOT_FOUND));

    ChattingRoom room =
        chattingRoomRepository
            .findByRoomId(chattingDto.getRoomId())
            .orElseThrow(() -> new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));

    if (!room.getParticipants().contains(user.getUserId())) {
      throw new CustomException(ChattingErrorCode.USER_NOT_IN_ROOM);
    }

    Chatting chatting = ChattingConvertUtil.convertToEntity(chattingDto, user);
    chattingRepository.save(chatting);
  }

  public void processAndSendMessage(ChattingDto chattingDto) {
    saveMessageAsync(chattingDto);
  }
}