package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.model.dto.chatting.*;
import com.example.algoyweb.service.chatting.ChattingService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @author JSW
 *
 * 채팅 기능 관련 API 를 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/algoy/api/chat")
@RequiredArgsConstructor
public class ChattingController {

  private final ChattingService chattingService;

  /**
   * 사용자의 채팅방 목록을 가져옵니다.
   *
   * @param authentication 사용자 인증 정보
   * @return 채팅방 목록 (ResponseEntity)
   */
  @GetMapping("/rooms")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<List<ChattingRoomDto>> getRooms(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return ResponseEntity.ok(chattingService.getUserRooms(userDetails.getUsername()));
  }

  /**
   * 특정 채팅방의 메시지 목록을 페이징 처리하여 가져옵니다.
   *
   * @param roomId 채팅방 ID
   * @param pageable 페이징 정보 (사용자가 선택한 페이지 번호, 정렬 기준 등)
   * @return 채팅 메시지 목록 페이지 (ResponseEntity)
   */
  @GetMapping("/room/{roomId}/messages")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<Page<ChattingDto>> getRoomMessages(
      @PathVariable String roomId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(chattingService.getRoomMessages(roomId, pageable));
  }

  /**
   * 새로운 채팅방을 생성합니다.
   *
   * @param request 채팅방 생성 요청 DTO
   * @param authentication 사용자 인증 정보
   * @return 생성된 채팅방 정보 (ResponseEntity)
   */
  @PostMapping("/room")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request, Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return ResponseEntity.ok(chattingService.createRoom(request.getName(), userDetails.getUsername(), request.getInvitees()));
  }

  /**
   * 기존 채팅방에 참여합니다.
   *
   * @param roomId 채팅방 ID
   * @param authentication 사용자 인증 정보
   * @return 참여한 채팅방 정보 (ResponseEntity)
   */
  @PostMapping("/room/{roomId}/join")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> joinRoom(@PathVariable String roomId, Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return ResponseEntity.ok(chattingService.joinRoom(roomId, userDetails.getUsername()));
  }

  /**
   * 채팅방에서 나갑니다.
   *
   * @param roomId 채팅방 ID
   * @param authentication 사용자 인증 정보
   * @return 나간 채팅방 정보 (ResponseEntity)
   */
  @PostMapping("/room/{roomId}/leave")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<ChattingRoomDto> leaveRoom(@PathVariable String roomId, Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return ResponseEntity.ok(chattingService.leaveRoom(roomId, userDetails.getUsername()));
  }

  /**
   * 채팅방에 사용자를 닉네임으로 초대합니다.
   *
   * @param roomId 채팅방 ID
   * @param inviteRequest 초대 요청 DTO
   * @param authentication 사용자 인증 정보
   * @return 초대 결과 (ResponseEntity)
   */
  @PostMapping("/room/{roomId}/invite-by-nickname")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
  public ResponseEntity<?> inviteToRoomByNickname(
      @PathVariable String roomId,
      @RequestBody InviteRequest inviteRequest,
      Authentication authentication) {
    try {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      chattingService.inviteUserByNickname(roomId, userDetails.getUsername(), inviteRequest.getNickname());
      return ResponseEntity.ok().body(Collections.singletonMap("message", "User invited successfully"));
    } catch (CustomException e) {
      return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(
          Map.of("code", e.getErrorCode().name(),
              "message", e.getErrorCode().getMessage())
      );
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(
          Map.of("code", "UNKNOWN_ERROR",
              "message", "An unexpected error occurred")
      );
    }
  }
}