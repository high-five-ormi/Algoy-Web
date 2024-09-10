package com.example.algoyweb.controller.chatting;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.ChattingErrorCode;
import com.example.algoyweb.model.dto.chatting.*;
import com.example.algoyweb.service.chatting.ChattingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChattingWebSocketControllerTest {

	@Mock
	private ChattingService chattingService;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private ChattingWebSocketController chattingWebSocketController;

	private MessageRequest createMessageRequest(String roomId, String content) throws Exception {
		MessageRequest request = new MessageRequest();
		Field roomIdField = MessageRequest.class.getDeclaredField("roomId");
		Field contentField = MessageRequest.class.getDeclaredField("content");
		roomIdField.setAccessible(true);
		contentField.setAccessible(true);
		roomIdField.set(request, roomId);
		contentField.set(request, content);
		return request;
	}

	@Test
	void testSendMessage() throws Exception {
		String roomId = "room1";
		String content = "Hello, World!";
		String username = "testUser";

		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(username);

		MessageRequest messageRequest = createMessageRequest(roomId, content);

		ChattingDto chattingDto = ChattingDto.builder()
			.userId(1L)
			.roomId(roomId)
			.content(content)
			.nickname(username)
			.createdAt(LocalDateTime.now())
			.build();

		when(chattingService.processAndSaveMessage(content, roomId, username))
			.thenReturn(chattingDto);

		chattingWebSocketController.sendMessage(messageRequest, principal);

		verify(chattingService).processAndSaveMessage(content, roomId, username);
		verify(messagingTemplate).convertAndSend("/topic/room/" + roomId, chattingDto);
	}

	@Test
	void testJoinRoom() {
		JoinRoomRequest joinRequest = new JoinRoomRequest();
		joinRequest.setRoomId("room1");
		joinRequest.setUsername("testUser");

		ChattingRoomDto roomDto = ChattingRoomDto.builder()
			.id(1L)
			.roomId("room1")
			.name("Test Room")
			.ownerId(1L)
			.build();

		when(chattingService.joinRoom("room1", "testUser")).thenReturn(roomDto);

		chattingWebSocketController.joinRoom(joinRequest);

		verify(chattingService).joinRoom("room1", "testUser");
		verify(messagingTemplate).convertAndSend(eq("/topic/room/room1"), any(JoinRoomResponse.class));
	}

	@Test
	void testLeaveRoom() {
		LeaveRoomRequest leaveRequest = new LeaveRoomRequest();
		leaveRequest.setRoomId("room1");
		leaveRequest.setUsername("testUser");

		ChattingRoomDto roomDto = ChattingRoomDto.builder()
			.id(1L)
			.roomId("room1")
			.name("Test Room")
			.ownerId(1L)
			.build();

		when(chattingService.leaveRoom("room1", "testUser")).thenReturn(roomDto);

		chattingWebSocketController.leaveRoom(leaveRequest);

		verify(chattingService).leaveRoom("room1", "testUser");
		verify(messagingTemplate).convertAndSend(eq("/topic/room/room1"), any(LeaveRoomResponse.class));
	}

	@Test
	void testSendMessage_WithEmptyContent() throws Exception {
		String roomId = "room1";
		String content = "";
		String username = "testUser";

		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(username);

		MessageRequest messageRequest = createMessageRequest(roomId, content);

		ChattingDto chattingDto = ChattingDto.builder()
			.userId(1L)
			.roomId(roomId)
			.content(content)
			.nickname(username)
			.createdAt(LocalDateTime.now())
			.build();

		when(chattingService.processAndSaveMessage(content, roomId, username))
			.thenReturn(chattingDto);

		chattingWebSocketController.sendMessage(messageRequest, principal);

		verify(chattingService).processAndSaveMessage(content, roomId, username);
		verify(messagingTemplate).convertAndSend("/topic/room/" + roomId, chattingDto);
	}

	@Test
	void testJoinRoom_RoomNotFound() {
		JoinRoomRequest joinRequest = new JoinRoomRequest();
		joinRequest.setRoomId("nonexistent_room");
		joinRequest.setUsername("testUser");

		when(chattingService.joinRoom("nonexistent_room", "testUser"))
			.thenThrow(new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));

		assertThrows(CustomException.class, () -> chattingWebSocketController.joinRoom(joinRequest));

		verify(chattingService).joinRoom("nonexistent_room", "testUser");
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}

	@Test
	void testLeaveRoom_UserNotInRoom() {
		LeaveRoomRequest leaveRequest = new LeaveRoomRequest();
		leaveRequest.setRoomId("room1");
		leaveRequest.setUsername("testUser");

		when(chattingService.leaveRoom("room1", "testUser"))
			.thenThrow(new CustomException(ChattingErrorCode.USER_NOT_IN_ROOM));

		assertThrows(CustomException.class, () -> chattingWebSocketController.leaveRoom(leaveRequest));

		verify(chattingService).leaveRoom("room1", "testUser");
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}

	@Test
	void testSendMessage_ExceedingMaxLength() throws Exception {
		String roomId = "room1";
		String content = "A".repeat(1001); // 1000자를 초과하는 메시지
		String username = "testUser";

		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(username);

		MessageRequest messageRequest = createMessageRequest(roomId, content);

		when(chattingService.processAndSaveMessage(content, roomId, username))
			.thenThrow(new CustomException(ChattingErrorCode.MESSAGE_TOO_LONG));

		assertThrows(CustomException.class, () ->
			chattingWebSocketController.sendMessage(messageRequest, principal));

		verify(chattingService).processAndSaveMessage(content, roomId, username);
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}

	@Test
	void testJoinRoom_AlreadyInRoom() {
		JoinRoomRequest joinRequest = new JoinRoomRequest();
		joinRequest.setRoomId("room1");
		joinRequest.setUsername("testUser");

		when(chattingService.joinRoom("room1", "testUser"))
			.thenThrow(new CustomException(ChattingErrorCode.USER_ALREADY_IN_ROOM));

		assertThrows(CustomException.class, () -> chattingWebSocketController.joinRoom(joinRequest));

		verify(chattingService).joinRoom("room1", "testUser");
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}
}