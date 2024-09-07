package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.JoinRoomRequest;
import com.example.algoyweb.model.dto.chatting.LeaveRoomRequest;
import com.example.algoyweb.service.chatting.ChattingService;
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

  @MessageMapping("/algoy/chat/sendMessage")
  public void sendMessage(@Payload ChattingDto chattingDto) {
    ChattingDto savedMessage = chattingService.saveAndSendMessage(chattingDto);
    messagingTemplate.convertAndSend("/topic/room/" + chattingDto.getRoomId(), savedMessage);
  }

  @MessageMapping("/algoy/chat/joinRoom")
  public void joinRoom(@Payload JoinRoomRequest joinRequest) {
    chattingService.joinRoom(joinRequest.getRoomId(), joinRequest.getUserId());
    messagingTemplate.convertAndSend("/topic/room/" + joinRequest.getRoomId(),
        "User " + joinRequest.getUserId() + " joined the room");
  }

  @MessageMapping("/algoy/chat/leaveRoom")
  public void leaveRoom(@Payload LeaveRoomRequest leaveRequest) {
    chattingService.leaveRoom(leaveRequest.getRoomId(), leaveRequest.getUserId());
    messagingTemplate.convertAndSend("/topic/room/" + leaveRequest.getRoomId(),
        "User " + leaveRequest.getUserId() + " left the room");
  }
}