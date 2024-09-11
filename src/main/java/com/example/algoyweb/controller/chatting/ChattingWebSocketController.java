package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.dto.chatting.JoinRoomRequest;
import com.example.algoyweb.model.dto.chatting.JoinRoomResponse;
import com.example.algoyweb.model.dto.chatting.LeaveRoomRequest;
import com.example.algoyweb.model.dto.chatting.LeaveRoomResponse;
import com.example.algoyweb.model.dto.chatting.MessageRequest;
import com.example.algoyweb.service.chatting.ChattingService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * @author JSW
 *
 * WebSocket 채팅 메시지 처리를 위한 컨트롤러입니다.
 * Spring Messaging 방식을 이용하여 클라이언트와 실시간 양방향 메시지 통신을 지원합니다.
 */
@Controller
@RequiredArgsConstructor
public class ChattingWebSocketController {

  private final ChattingService chattingService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * 클라이언트에서 "/chat/sendMessage" 에 메시지를 전송하면 호출되는 메서드입니다.
   *
   * @param messageRequest 메시지 내용과 채팅방 ID를 포함하는 DTO
   * @param principal 로그인한 사용자 정보 (Principal 객체)
   */
  @MessageMapping("/chat/sendMessage")
  public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
    ChattingDto chattingDto =
        chattingService.processAndSaveMessage(
            messageRequest.getContent(), messageRequest.getRoomId(), principal.getName());

    // 메시지를 브로커를 통해 해당 채팅방 (/topic/room/ + roomId) 에 구독하는 모든 클라이언트에게 전송
    messagingTemplate.convertAndSend("/topic/room/" + chattingDto.getRoomId(), chattingDto);
  }


  /**
   * 클라이언트에서 "/algoy/chat/joinRoom" 에 채팅방 참여 요청을 보내면 호출되는 메서드입니다.
   *
   * @param joinRequest 채팅방 ID와 사용자 이름을 포함하는 JoinRoomRequest DTO
   */
  @MessageMapping("/algoy/chat/joinRoom")
  public void joinRoom(@Valid @Payload JoinRoomRequest joinRequest) {
    ChattingRoomDto roomDto =
        chattingService.joinRoom(joinRequest.getRoomId(), joinRequest.getUsername());

    // 채팅방 정보를 포함한 JoinRoomResponse 객체 생성
    JoinRoomResponse response =
        new JoinRoomResponse(
            roomDto.getName(), joinRequest.getUsername() + " joined the room", roomDto);

    // 해당 채팅방 (/topic/room/ + roomId) 에 구독하는 모든 클라이언트에게 참여 메시지 전송
    messagingTemplate.convertAndSend("/topic/room/" + joinRequest.getRoomId(), response);
  }

  /**
   * 클라이언트에서 "/algoy/chat/leaveRoom" 에 채팅방 나가기 요청을 보내면 호출되는 메서드입니다.
   *
   * @param leaveRequest 채팅방 ID와 사용자 이름을 포함하는 LeaveRoomRequest DTO
   */
  @MessageMapping("/algoy/chat/leaveRoom")
  public void leaveRoom(@Valid @Payload LeaveRoomRequest leaveRequest) {
    ChattingRoomDto roomDto =
        chattingService.leaveRoom(leaveRequest.getRoomId(), leaveRequest.getUsername());

    // 채팅방 정보를 포함한 LeaveRoomResponse 객체 생성
    LeaveRoomResponse response =
        new LeaveRoomResponse(
            roomDto.getName(), leaveRequest.getUsername() + " left the room", roomDto);

    // 해당 채팅방 (/topic/room/ + roomId) 에 구독하는 모든 클라이언트에게 나가기 메시지 전송
    messagingTemplate.convertAndSend("/topic/room/" + leaveRequest.getRoomId(), response);
  }
}