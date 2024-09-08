package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.model.dto.chatting.*;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.service.chatting.ChattingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  public ResponseEntity<List<ChattingRoomDto>> getRooms(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(chattingService.getUserRooms(user.getEmail()));
  }

  @GetMapping("/room/{roomId}/messages")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingDto>> getRoomMessages(
      @PathVariable String roomId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(chattingService.getRoomMessages(roomId, pageable));
  }

  @PostMapping("/room")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(chattingService.createRoom(request.getName(), user.getEmail()));
  }

  @PostMapping("/room/{roomId}/join")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> joinRoom(@PathVariable String roomId, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    chattingService.joinRoom(roomId, user.getUserId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/leave")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> leaveRoom(@PathVariable String roomId, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    chattingService.leaveRoom(roomId, user.getUserId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/room/{roomId}/invite-by-nickname")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Void> inviteToRoomByNickname(@PathVariable String roomId, @RequestBody String inviteeNickname, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    chattingService.inviteUserByNickname(roomId, user.getEmail(), inviteeNickname);
    return ResponseEntity.ok().build();
  }
}