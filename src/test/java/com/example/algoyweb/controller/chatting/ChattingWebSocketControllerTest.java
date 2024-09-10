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

/**
 * @author JSW
 *
 * ChattingWebSocketController의 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ChattingWebSocketControllerTest {

	@Mock
	private ChattingService chattingService;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private ChattingWebSocketController chattingWebSocketController;

	/**
	 * 테스트용 MessageRequest 객체를 생성합니다.
	 */
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

	/**
	 * 메시지 전송 기능을 테스트합니다.
	 */
	@Test
	void testSendMessage() throws Exception {
		// Given
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

		// When
		chattingWebSocketController.sendMessage(messageRequest, principal);

		// Then
		verify(chattingService).processAndSaveMessage(content, roomId, username);
		verify(messagingTemplate).convertAndSend("/topic/room/" + roomId, chattingDto);
	}

	/**
	 * 채팅방 입장 기능을 테스트합니다.
	 */
	@Test
	void testJoinRoom() {
		// Given
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

		// When
		chattingWebSocketController.joinRoom(joinRequest);

		// Then
		verify(chattingService).joinRoom("room1", "testUser");
		verify(messagingTemplate).convertAndSend(eq("/topic/room/room1"), any(JoinRoomResponse.class));
	}

	/**
	 * 채팅방 퇴장 기능을 테스트합니다.
	 */
	@Test
	void testLeaveRoom() {
		// Given
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

		// When
		chattingWebSocketController.leaveRoom(leaveRequest);

		// Then
		verify(chattingService).leaveRoom("room1", "testUser");
		verify(messagingTemplate).convertAndSend(eq("/topic/room/room1"), any(LeaveRoomResponse.class));
	}

	/**
	 * 빈 내용의 메시지 전송을 테스트합니다.
	 */
	@Test
	void testSendMessage_WithEmptyContent() throws Exception {
		// Given
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

		// When
		chattingWebSocketController.sendMessage(messageRequest, principal);

		// Then
		verify(chattingService).processAndSaveMessage(content, roomId, username);
		verify(messagingTemplate).convertAndSend("/topic/room/" + roomId, chattingDto);
	}

	/**
	 * 존재하지 않는 채팅방 입장 시도를 테스트합니다.
	 */
	@Test
	void testJoinRoom_RoomNotFound() {
		// Given
		JoinRoomRequest joinRequest = new JoinRoomRequest();
		joinRequest.setRoomId("nonexistent_room");
		joinRequest.setUsername("testUser");

		when(chattingService.joinRoom("nonexistent_room", "testUser"))
			.thenThrow(new CustomException(ChattingErrorCode.ROOM_NOT_FOUND));

		// When & Then
		assertThrows(CustomException.class, () -> chattingWebSocketController.joinRoom(joinRequest));

		verify(chattingService).joinRoom("nonexistent_room", "testUser");
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}

	/**
	 * 채팅방에 없는 사용자의 퇴장 시도를 테스트합니다.
	 */
	@Test
	void testLeaveRoom_UserNotInRoom() {
		// Given
		LeaveRoomRequest leaveRequest = new LeaveRoomRequest();
		leaveRequest.setRoomId("room1");
		leaveRequest.setUsername("testUser");

		when(chattingService.leaveRoom("room1", "testUser"))
			.thenThrow(new CustomException(ChattingErrorCode.USER_NOT_IN_ROOM));

		// When & Then
		assertThrows(CustomException.class, () -> chattingWebSocketController.leaveRoom(leaveRequest));

		verify(chattingService).leaveRoom("room1", "testUser");
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}

	/**
	 * 최대 길이를 초과하는 메시지 전송을 테스트합니다.
	 */
	@Test
	void testSendMessage_ExceedingMaxLength() throws Exception {
		// Given
		String roomId = "room1";
		String content = "A".repeat(1001); // 1000자를 초과하는 메시지
		String username = "testUser";

		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(username);

		MessageRequest messageRequest = createMessageRequest(roomId, content);

		when(chattingService.processAndSaveMessage(content, roomId, username))
			.thenThrow(new CustomException(ChattingErrorCode.MESSAGE_TOO_LONG));

		// When & Then
		assertThrows(CustomException.class, () ->
			chattingWebSocketController.sendMessage(messageRequest, principal));

		verify(chattingService).processAndSaveMessage(content, roomId, username);
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}

	/**
	 * 이미 채팅방에 있는 사용자의 입장 시도를 테스트합니다.
	 */
	@Test
	void testJoinRoom_AlreadyInRoom() {
		// Given
		JoinRoomRequest joinRequest = new JoinRoomRequest();
		joinRequest.setRoomId("room1");
		joinRequest.setUsername("testUser");

		when(chattingService.joinRoom("room1", "testUser"))
			.thenThrow(new CustomException(ChattingErrorCode.USER_ALREADY_IN_ROOM));

		// When & Then
		assertThrows(CustomException.class, () -> chattingWebSocketController.joinRoom(joinRequest));

		verify(chattingService).joinRoom("room1", "testUser");
		verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
	}
}