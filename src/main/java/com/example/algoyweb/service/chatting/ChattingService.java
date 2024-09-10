package com.example.algoyweb.service.chatting;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.ChattingErrorCode;
import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.chatting.ChattingRepository;
import com.example.algoyweb.repository.chatting.ChattingRoomRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.chatting.ChattingConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChattingService {

  private final ChattingRepository chattingRepository;
  private final ChattingRoomRepository chattingRoomRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @Transactional(readOnly = true)
  public List<ChattingRoomDto> getUserRooms(String username) {
    User user = getUserByUsername(username);
    List<ChattingRoom> rooms = chattingRoomRepository.findByUserIdInParticipantsOrOwnerId(user.getUserId());
    return rooms.stream().map(ChattingConvertUtil::convertToDto).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Page<ChattingDto> getRoomMessages(String roomId, Pageable pageable) {
    Page<Chatting> messages = chattingRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
    return messages.map(ChattingConvertUtil::convertToDto);
  }

  public ChattingRoomDto createRoom(String roomName, String ownerUsername, List<String> inviteeNicknames) {
    User owner = getUserByUsername(ownerUsername);

    if (roomName == null || roomName.trim().isEmpty()) {
      throw new CustomException(ChattingErrorCode.INVALID_ROOM_NAME);
    }

    if (inviteeNicknames == null || inviteeNicknames.isEmpty()) {
      throw new CustomException(ChattingErrorCode.NO_INVITEES);
    }

    ChattingRoom chattingRoom = ChattingRoom.builder()
        .roomId("room-" + System.currentTimeMillis())
        .name(roomName)
        .owner(owner)
        .participants(new ArrayList<>(List.of(owner.getUserId())))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    for (String nickname : inviteeNicknames) {
      if (!nickname.equals(owner.getNickname())) {
        User invitee = userRepository.findByNickname(nickname);
        if (invitee != null) {
          chattingRoom.addParticipant(invitee.getUserId());
        }
      }
    }

    ChattingRoom savedRoom = chattingRoomRepository.save(chattingRoom);
    return ChattingConvertUtil.convertToDto(savedRoom);
  }

  public ChattingRoomDto joinRoom(String roomId, String username) {
    User user = getUserByUsername(username);
    ChattingRoom room = getChattingRoomByRoomId(roomId);
    if (!room.getParticipants().contains(user.getUserId())) {
      room.addParticipant(user.getUserId());
      room = chattingRoomRepository.save(room);
    }
    sendSystemMessage(roomId, username + "님이 입장하셨습니다.");
    return ChattingConvertUtil.convertToDto(room);
  }

  public ChattingRoomDto leaveRoom(String roomId, String username) {
    User user = getUserByUsername(username);
    ChattingRoom room = getChattingRoomByRoomId(roomId);

    if (!room.isParticipant(user.getUserId())) {
      throw new CustomException(ChattingErrorCode.USER_NOT_IN_ROOM);
    }

    room.removeParticipant(user.getUserId());

    // 방장이 나가는 경우
    if (room.getOwner().getUserId().equals(user.getUserId())) {
      if (!room.isEmpty()) {
        // 다른 참가자를 방장으로 지정
        User newOwner = userRepository.findById(room.getParticipants().get(0))
            .orElseThrow(() -> new CustomException(ChattingErrorCode.USER_NOT_FOUND));
        room.changeOwner(newOwner);
      } else {
        // 마지막 참가자가 나가는 경우 방 삭제
        chattingRoomRepository.delete(room);
        sendSystemMessage(roomId, username + "님이 퇴장하셨습니다.");
        return ChattingRoomDto.builder()
            .roomId(roomId)
            .deleted(true)
            .build(); // 방이 삭제되었음을 나타내는 특별한 DTO 반환
      }
    }

    room = chattingRoomRepository.save(room);
    sendSystemMessage(roomId, username + "님이 퇴장하셨습니다.");
    return ChattingConvertUtil.convertToDto(room);
  }

  public void inviteUserByNickname(String roomId, String inviterUsername, String inviteeNickname) {
    User inviter = getUserByUsername(inviterUsername);
    ChattingRoom room = getChattingRoomByRoomId(roomId);

    if (!room.getOwner().getUserId().equals(inviter.getUserId())) {
      throw new CustomException(ChattingErrorCode.NOT_ROOM_OWNER);
    }

    if (inviter.getNickname().equals(inviteeNickname)) {
      throw new CustomException(ChattingErrorCode.SELF_INVITATION_NOT_ALLOWED);
    }

    User invitee = userRepository.findByNickname(inviteeNickname);
    if (invitee == null) {
      throw new CustomException(ChattingErrorCode.USER_NOT_FOUND);
    }

    if (room.getParticipants().contains(invitee.getUserId())) {
      throw new CustomException(ChattingErrorCode.USER_ALREADY_IN_ROOM);
    }

    room.addParticipant(invitee.getUserId());
    chattingRoomRepository.save(room);

    // 초대 메시지 전송
    sendSystemMessage(roomId, inviteeNickname + "님이 초대되었습니다.");
  }

  public ChattingDto processAndSaveMessage(String content, String roomId, String username) {
    if (content.length() > 1000) {
      throw new CustomException(ChattingErrorCode.MESSAGE_TOO_LONG);
    }

    User user = getUserByUsername(username);
    ChattingRoom room = getChattingRoomByRoomId(roomId);

    if (!room.getParticipants().contains(user.getUserId())) {
      throw new CustomException(ChattingErrorCode.USER_NOT_IN_ROOM);
    }

    ChattingDto chattingDto = ChattingDto.builder()
        .userId(user.getUserId())
        .roomId(roomId)
        .content(content)
        .nickname(user.getNickname())
        .createdAt(LocalDateTime.now())
        .build();

    Chatting chatting = ChattingConvertUtil.convertToEntity(chattingDto, user);
    chattingRepository.save(chatting);

    return chattingDto;
  }

  private void sendSystemMessage(String roomId, String messageContent) {
    ChattingDto systemMessage = ChattingDto.builder()
        .userId(null)
        .roomId(roomId)
        .content(messageContent)
        .nickname("System")
        .createdAt(LocalDateTime.now())
        .build();

    messagingTemplate.convertAndSend("/topic/room/" + roomId, systemMessage);
  }

  private User getUserByUsername(String username) {
    return userRepository.findByEmail(username);
  }

  private ChattingRoom getChattingRoomByRoomId(String roomId) {
    return chattingRoomRepository.findByRoomId(roomId)
        .orElseThrow(() -> new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));
  }
}