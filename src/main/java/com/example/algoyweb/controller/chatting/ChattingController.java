package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.dto.chatting.JoinRoomRequest;
import com.example.algoyweb.model.dto.chatting.LeaveRoomRequest;
import com.example.algoyweb.service.chatting.ChattingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/algoy/api/chat")
@RequiredArgsConstructor
public class ChattingController {

  private final ChattingService chattingService;

  @GetMapping("/room/{roomId}/messages")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingDto>> getRoomMessages(
      @PathVariable String roomId,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<ChattingDto> messages = chattingService.getRoomMessages(roomId, pageable);
    return ResponseEntity.ok(messages);
  }

  @PostMapping("/room")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> createRoom(@Valid @RequestBody ChattingRoomDto roomDto) {
    ChattingRoomDto createdRoom = chattingService.createRoom(roomDto.getName());
    return ResponseEntity.ok(createdRoom);
  }

  @PostMapping("/room/{roomId}/join")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> joinRoom(@PathVariable String roomId, @Valid @RequestBody JoinRoomRequest joinRequest) {
    chattingService.joinRoom(roomId, joinRequest.getUserId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/leave")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, @Valid @RequestBody LeaveRoomRequest leaveRequest) {
    chattingService.leaveRoom(roomId, leaveRequest.getUserId());
    return ResponseEntity.ok().build();
  }
}