package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.*;
import com.example.algoyweb.service.chatting.ChattingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/algoy/api/chat")
@RequiredArgsConstructor
public class ChattingController {

  private final ChattingService chattingService;

  @GetMapping("/rooms")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingRoomDto>> getAllRooms(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(chattingService.getAllRooms(pageable));
  }

  @GetMapping("/my-rooms")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingRoomDto>> getMyRooms(
      @PageableDefault(size = 20) Pageable pageable,
      Authentication authentication) {
    return ResponseEntity.ok(chattingService.getRoomsForUser(authentication.getName(), pageable));
  }

  @GetMapping("/room/{roomId}/messages")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingDto>> getRoomMessages(
      @PathVariable String roomId,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(chattingService.getRoomMessages(roomId, pageable));
  }

  @PostMapping("/room")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request, Authentication authentication) {
    return ResponseEntity.ok(chattingService.createRoom(request.getName(), authentication.getName()));
  }

  @PostMapping("/room/{roomId}/join")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> joinRoom(@PathVariable String roomId, Authentication authentication) {
    chattingService.joinRoom(roomId, authentication.getName());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/leave")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, Authentication authentication) {
    chattingService.leaveRoom(roomId, authentication.getName());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/invite")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> inviteToRoom(@PathVariable String roomId, @Valid @RequestBody InviteRequest inviteRequest, Authentication authentication) {
    chattingService.inviteToRoom(roomId, authentication.getName(), inviteRequest.getInviteeId());
    return ResponseEntity.ok().build();
  }
}