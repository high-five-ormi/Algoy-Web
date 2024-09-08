package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.*;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;
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
  private final UserRepository userRepository;

  @GetMapping("/room/{roomId}/messages")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingDto>> getRoomMessages(
      @PathVariable String roomId,
      @PageableDefault(size = 20) Pageable pageable,
      Authentication authentication) {
    String username = authentication.getName();
    User user = getUserByUsername(username);
    Page<ChattingDto> messages = chattingService.getRoomMessages(roomId, pageable);
    return ResponseEntity.ok(messages);
  }

  @PostMapping("/room")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request, Authentication authentication) {
    String username = authentication.getName();
    User user = getUserByUsername(username);
    ChattingRoomDto createdRoom = chattingService.createRoom(request.getName(), user.getUserId());
    return ResponseEntity.ok(createdRoom);
  }

  @PostMapping("/room/{roomId}/join")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> joinRoom(@PathVariable String roomId, Authentication authentication) {
    String username = authentication.getName();
    User user = getUserByUsername(username);
    chattingService.joinRoom(roomId, user.getUserId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/leave")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, Authentication authentication) {
    String username = authentication.getName();
    User user = getUserByUsername(username);
    chattingService.leaveRoom(roomId, user.getUserId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/invite")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> inviteToRoom(@PathVariable String roomId, @Valid @RequestBody InviteRequest inviteRequest, Authentication authentication) {
    String username = authentication.getName();
    User user = getUserByUsername(username);
    chattingService.inviteToRoom(roomId, user.getUserId(), inviteRequest.getInviteeId());
    return ResponseEntity.ok().build();
  }

  private User getUserByUsername(String username) {
    return userRepository.findByEmail(username);
  }
}