package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.JoinRoomRequest;
import com.example.algoyweb.model.dto.chatting.LeaveRoomRequest;
import com.example.algoyweb.model.dto.chatting.MessageRequest;
import com.example.algoyweb.service.chatting.ChattingService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChattingWebSocketController {

  private final ChattingService chattingService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat/sendMessage")
  public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
    ChattingDto chattingDto = chattingService.processAndSaveMessage(
        messageRequest.getContent(),
        messageRequest.getRoomId(),
        principal.getName()
    );

    messagingTemplate.convertAndSend("/topic/room/" + chattingDto.getRoomId(), chattingDto);
  }

  @MessageMapping("/algoy/chat/joinRoom")
  public void joinRoom(@Valid @Payload JoinRoomRequest joinRequest) {
    String nickname = chattingService.joinRoom(joinRequest.getRoomId(), joinRequest.getUsername());
    messagingTemplate.convertAndSend(
        "/topic/room/" + joinRequest.getRoomId(),
        nickname + " joined the room");
  }

  @MessageMapping("/algoy/chat/leaveRoom")
  public void leaveRoom(@Valid @Payload LeaveRoomRequest leaveRequest) {
    String nickname = chattingService.leaveRoom(leaveRequest.getRoomId(), leaveRequest.getUsername());
    messagingTemplate.convertAndSend(
        "/topic/room/" + leaveRequest.getRoomId(),
        nickname + " left the room");
  }
}