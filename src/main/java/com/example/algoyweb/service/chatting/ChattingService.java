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

/**
 * @author JSW
 *
 * 채팅 기능을 관리하는 서비스 클래스입니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ChattingService {

  private final ChattingRepository chattingRepository;
  private final ChattingRoomRepository chattingRoomRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * 주어진 사용자의 모든 채팅방을 조회합니다.
   *
   * @param username 사용자의 username
   * @return 사용자의 채팅방을 나타내는 ChattingRoomDto 객체 리스트
   */
  @Transactional(readOnly = true)
  public List<ChattingRoomDto> getUserRooms(String username) {
    User user = getUserByUsername(username);
    List<ChattingRoom> rooms = chattingRoomRepository.findByUserIdInParticipantsOrOwnerId(user.getUserId());
    return rooms.stream().map(ChattingConvertUtil::convertToDto).collect(Collectors.toList());
  }

  /**
   * 특정 채팅방의 메시지를 조회합니다.
   *
   * @param roomId 채팅방 ID
   * @param pageable 페이지네이션 정보
   * @return 해당 방의 메시지를 나타내는 ChattingDto 객체의 Page
   */
  @Transactional(readOnly = true)
  public Page<ChattingDto> getRoomMessages(String roomId, Pageable pageable) {
    Page<Chatting> messages = chattingRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
    return messages.map(ChattingConvertUtil::convertToDto);
  }

  /**
   * 새로운 채팅방을 생성합니다.
   *
   * @param roomName 새 방의 이름
   * @param ownerUsername 방 소유자의 username
   * @param inviteeNicknames 방에 초대할 사용자들의 닉네임 리스트
   * @return 새로 생성된 방을 나타내는 ChattingRoomDto 객체
   */
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

  /**
   * 사용자를 채팅방에 참여시킵니다.
   *
   * @param roomId 참여할 방의 ID
   * @param username 참여하는 사용자의 username
   * @return 업데이트된 방을 나타내는 ChattingRoomDto 객체
   */
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

  /**
   * 사용자를 채팅방에서 퇴장시킵니다.
   *
   * @param roomId 퇴장할 방의 ID
   * @param username 퇴장하는 사용자의 username
   * @return 업데이트된 방을 나타내는 ChattingRoomDto 객체
   */
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

  /**
   * 닉네임으로 사용자를 채팅방에 초대합니다.
   *
   * @param roomId 초대할 방의 ID
   * @param inviterUsername 초대하는 사용자의 username
   * @param inviteeNickname 초대받는 사용자의 닉네임
   */
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

  /**
   * 메시지를 처리하고 저장합니다.
   *
   * @param content 메시지 내용
   * @param roomId 메시지를 보낼 방의 ID
   * @param username 메시지를 보내는 사용자의 username
   * @return 저장된 메시지를 나타내는 ChattingDto 객체
   */
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

  /**
   * 시스템 메시지를 전송합니다.
   *
   * @param roomId 메시지를 보낼 방의 ID
   * @param messageContent 메시지 내용
   */
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

  /**
   * username으로 User 객체를 조회합니다.
   *
   * @param username 조회할 사용자의 username
   * @return 조회된 User 객체
   */
  private User getUserByUsername(String username) {
    return userRepository.findByEmail(username);
  }

  /**
   * roomId로 ChattingRoom 객체를 조회합니다.
   *
   * @param roomId 조회할 방의 ID
   * @return 조회된 ChattingRoom 객체
   */
  private ChattingRoom getChattingRoomByRoomId(String roomId) {
    return chattingRoomRepository.findByRoomId(roomId)
        .orElseThrow(() -> new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));
  }
}