package com.example.algoyweb.controller.chatting;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.ChattingErrorCode;
import com.example.algoyweb.model.dto.chatting.*;
import com.example.algoyweb.service.chatting.ChattingService;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author JSW
 *
 * ChattingController의 단위 테스트
 */
class ChattingControllerTest {

  @Mock private ChattingService chattingService;

  @Mock private Authentication authentication;

  @Mock private UserDetails userDetails;

  @InjectMocks private ChattingController chattingController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn("testUser");
  }

  /**
   * 사용자의 채팅방 목록을 정상적으로 가져오는지 확인하는 테스트
   */
  @Test
  void testGetRooms() {
    // Given
    List<ChattingRoomDto> rooms =
        Arrays.asList(
            ChattingRoomDto.builder()
                .id(1L)
                .roomId("room1")
                .name("Test Room 1")
                .ownerId(1L)
                .participants(Arrays.asList(1L, 2L))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
            ChattingRoomDto.builder()
                .id(2L)
                .roomId("room2")
                .name("Test Room 2")
                .ownerId(2L)
                .participants(Arrays.asList(1L, 2L, 3L))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    when(chattingService.getUserRooms("testUser")).thenReturn(rooms);

    // When
    ResponseEntity<List<ChattingRoomDto>> response = chattingController.getRooms(authentication);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(rooms, response.getBody());
    verify(chattingService).getUserRooms("testUser");
  }

  /**
   * 특정 채팅방의 메시지를 페이지네이션과 함께 정상적으로 가져오는지 확인하는 테스트
   */
  @Test
  void testGetRoomMessages() {
    // Given
    String roomId = "room1";
    Page<ChattingDto> messages =
        new PageImpl<>(
            Arrays.asList(
                ChattingDto.builder()
                    .userId(1L)
                    .roomId(roomId)
                    .content("Hello")
                    .nickname("user1")
                    .createdAt(LocalDateTime.now())
                    .build(),
                ChattingDto.builder()
                    .userId(2L)
                    .roomId(roomId)
                    .content("Hi there")
                    .nickname("user2")
                    .createdAt(LocalDateTime.now())
                    .build()));
    when(chattingService.getRoomMessages(eq(roomId), any(Pageable.class))).thenReturn(messages);

    // When
    ResponseEntity<Page<ChattingDto>> response =
        chattingController.getRoomMessages(roomId, Pageable.unpaged());

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(messages, response.getBody());
    verify(chattingService).getRoomMessages(eq(roomId), any(Pageable.class));
  }

  /**
   * 새로운 채팅방 생성이 정상적으로 이루어지는지 확인하는 테스트
   */
  @Test
  void testCreateRoom() {
    // Given
    CreateRoomRequest request = new CreateRoomRequest();
    request.setName("New Room");
    request.setInvitees(Arrays.asList("user1", "user2"));

    ChattingRoomDto createdRoom =
        ChattingRoomDto.builder()
            .id(3L)
            .roomId("room3")
            .name("New Room")
            .ownerId(1L)
            .participants(Arrays.asList(1L, 2L, 3L))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(chattingService.createRoom(eq("New Room"), eq("testUser"), anyList()))
        .thenReturn(createdRoom);

    // When
    ResponseEntity<ChattingRoomDto> response =
        chattingController.createRoom(request, authentication);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(createdRoom, response.getBody());
    verify(chattingService).createRoom(eq("New Room"), eq("testUser"), anyList());
  }

  /**
   * 사용자가 채팅방에 정상적으로 참여할 수 있는지 확인하는 테스트
   */
  @Test
  void testJoinRoom() {
    // Given
    String roomId = "room1";
    ChattingRoomDto joinedRoom =
        ChattingRoomDto.builder()
            .id(1L)
            .roomId(roomId)
            .name("Test Room 1")
            .ownerId(1L)
            .participants(Arrays.asList(1L, 2L, 3L))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(chattingService.joinRoom(roomId, "testUser")).thenReturn(joinedRoom);

    // When
    ResponseEntity<ChattingRoomDto> response = chattingController.joinRoom(roomId, authentication);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(joinedRoom, response.getBody());
    verify(chattingService).joinRoom(roomId, "testUser");
  }

  /**
   * 사용자가 채팅방을 정상적으로 나갈 수 있는지 확인하는 테스트
   */
  @Test
  void testLeaveRoom() {
    // Given
    String roomId = "room1";
    ChattingRoomDto leftRoom =
        ChattingRoomDto.builder()
            .id(1L)
            .roomId(roomId)
            .name("Test Room 1")
            .ownerId(1L)
            .participants(Arrays.asList(1L, 2L))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(chattingService.leaveRoom(roomId, "testUser")).thenReturn(leftRoom);

    // When
    ResponseEntity<ChattingRoomDto> response = chattingController.leaveRoom(roomId, authentication);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(leftRoom, response.getBody());
    verify(chattingService).leaveRoom(roomId, "testUser");
  }

  /**
   * 사용자가 다른 사용자를 채팅방에 초대할 수 있는지 확인하는 테스트
   */
  @Test
  void testInviteToRoomByNickname() {
    // Given
    String roomId = "room1";
    InviteRequest inviteRequest = new InviteRequest();
    inviteRequest.setNickname("invitedUser");

    doNothing().when(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");

    // When
    ResponseEntity<?> response =
        chattingController.inviteToRoomByNickname(roomId, inviteRequest, authentication);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("User invited successfully", ((java.util.Map) response.getBody()).get("message"));
    verify(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");
  }

  /**
   * 존재하지 않는 사용자를 초대할 때 예외 처리가 정상적으로 이루어지는지 확인하는 테스트
   */
  @Test
  void testInviteToRoomByNickname_Exception() {
    // Given
    String roomId = "room1";
    InviteRequest inviteRequest = new InviteRequest();
    inviteRequest.setNickname("invitedUser");

    CustomException expectedException = new CustomException(ChattingErrorCode.USER_NOT_FOUND);
    doThrow(expectedException)
        .when(chattingService)
        .inviteUserByNickname(roomId, "testUser", "invitedUser");

    // When
    ResponseEntity<?> response =
        chattingController.inviteToRoomByNickname(roomId, inviteRequest, authentication);

    // Then
    assertEquals(ChattingErrorCode.USER_NOT_FOUND.getHttpStatus().value(), response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof Map);
    Map<String, String> responseBody = (Map<String, String>) response.getBody();
    assertEquals(ChattingErrorCode.USER_NOT_FOUND.name(), responseBody.get("code"));
    assertEquals(ChattingErrorCode.USER_NOT_FOUND.getMessage(), responseBody.get("message"));
    verify(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");
  }
}