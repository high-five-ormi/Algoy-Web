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

class ChattingControllerTest {

	@Mock
	private ChattingService chattingService;

	@Mock
	private Authentication authentication;

	@Mock
	private UserDetails userDetails;

	@InjectMocks
	private ChattingController chattingController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(userDetails.getUsername()).thenReturn("testUser");
	}

	@Test
	void testGetRooms() {
		List<ChattingRoomDto> rooms = Arrays.asList(
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
				.build()
		);
		when(chattingService.getUserRooms("testUser")).thenReturn(rooms);

		ResponseEntity<List<ChattingRoomDto>> response = chattingController.getRooms(authentication);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(rooms, response.getBody());
		verify(chattingService).getUserRooms("testUser");
	}

	@Test
	void testGetRoomMessages() {
		String roomId = "room1";
		Page<ChattingDto> messages = new PageImpl<>(Arrays.asList(
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
				.build()
		));
		when(chattingService.getRoomMessages(eq(roomId), any(Pageable.class))).thenReturn(messages);

		ResponseEntity<Page<ChattingDto>> response = chattingController.getRoomMessages(roomId, Pageable.unpaged());

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(messages, response.getBody());
		verify(chattingService).getRoomMessages(eq(roomId), any(Pageable.class));
	}

	@Test
	void testCreateRoom() {
		CreateRoomRequest request = new CreateRoomRequest();
		request.setName("New Room");
		request.setInvitees(Arrays.asList("user1", "user2"));

		ChattingRoomDto createdRoom = ChattingRoomDto.builder()
			.id(3L)
			.roomId("room3")
			.name("New Room")
			.ownerId(1L)
			.participants(Arrays.asList(1L, 2L, 3L))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		when(chattingService.createRoom(eq("New Room"), eq("testUser"), anyList())).thenReturn(createdRoom);

		ResponseEntity<ChattingRoomDto> response = chattingController.createRoom(request, authentication);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(createdRoom, response.getBody());
		verify(chattingService).createRoom(eq("New Room"), eq("testUser"), anyList());
	}

	@Test
	void testJoinRoom() {
		String roomId = "room1";
		ChattingRoomDto joinedRoom = ChattingRoomDto.builder()
			.id(1L)
			.roomId(roomId)
			.name("Test Room 1")
			.ownerId(1L)
			.participants(Arrays.asList(1L, 2L, 3L))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		when(chattingService.joinRoom(roomId, "testUser")).thenReturn(joinedRoom);

		ResponseEntity<ChattingRoomDto> response = chattingController.joinRoom(roomId, authentication);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(joinedRoom, response.getBody());
		verify(chattingService).joinRoom(roomId, "testUser");
	}

	@Test
	void testLeaveRoom() {
		String roomId = "room1";
		ChattingRoomDto leftRoom = ChattingRoomDto.builder()
			.id(1L)
			.roomId(roomId)
			.name("Test Room 1")
			.ownerId(1L)
			.participants(Arrays.asList(1L, 2L))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		when(chattingService.leaveRoom(roomId, "testUser")).thenReturn(leftRoom);

		ResponseEntity<ChattingRoomDto> response = chattingController.leaveRoom(roomId, authentication);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals(leftRoom, response.getBody());
		verify(chattingService).leaveRoom(roomId, "testUser");
	}

	@Test
	void testInviteToRoomByNickname() {
		String roomId = "room1";
		InviteRequest inviteRequest = new InviteRequest();
		inviteRequest.setNickname("invitedUser");

		doNothing().when(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");

		ResponseEntity<?> response = chattingController.inviteToRoomByNickname(roomId, inviteRequest, authentication);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("User invited successfully", ((java.util.Map)response.getBody()).get("message"));
		verify(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");
	}

	@Test
	void testInviteToRoomByNickname_Exception() {
		String roomId = "room1";
		InviteRequest inviteRequest = new InviteRequest();
		inviteRequest.setNickname("invitedUser");

		CustomException expectedException = new CustomException(ChattingErrorCode.USER_NOT_FOUND);
		doThrow(expectedException)
			.when(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");

		ResponseEntity<?> response = chattingController.inviteToRoomByNickname(roomId, inviteRequest, authentication);

		assertEquals(400, response.getStatusCodeValue());
		assertNotNull(response.getBody());
		assertTrue(response.getBody() instanceof Map);
		Map<String, String> responseBody = (Map<String, String>) response.getBody();
		assertEquals(expectedException.getMessage(), responseBody.get("error"));
		verify(chattingService).inviteUserByNickname(roomId, "testUser", "invitedUser");
	}
}