package com.example.algoyweb.service.chatting;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.ChattingErrorCode;
import com.example.algoyweb.model.dto.chatting.ChattingDto;
import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.chatting.ChattingRepository;
import com.example.algoyweb.repository.chatting.ChattingRoomRepository;
import com.example.algoyweb.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChattingServiceTest {

	@Mock
	private ChattingRepository chattingRepository;

	@Mock
	private ChattingRoomRepository chattingRoomRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private ChattingService chattingService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getUserRooms_ShouldReturnUserRooms() {
		// Arrange
		User user = User.builder().userId(1L).build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(user);

		ChattingRoom room1 = ChattingRoom.builder().roomId("room1").name("Room 1").build();
		ChattingRoom room2 = ChattingRoom.builder().roomId("room2").name("Room 2").build();
		List<ChattingRoom> rooms = Arrays.asList(room1, room2);

		when(chattingRoomRepository.findByUserIdInParticipantsOrOwnerId(1L)).thenReturn(rooms);

		// Act
		List<ChattingRoomDto> result = chattingService.getUserRooms("test@example.com");

		// Assert
		assertEquals(2, result.size());
		assertEquals("room1", result.get(0).getRoomId());
		assertEquals("room2", result.get(1).getRoomId());
	}

	@Test
	void getRoomMessages_ShouldReturnMessages() {
		// Arrange
		String roomId = "room1";
		Pageable pageable = mock(Pageable.class);

		User user1 = User.builder()
			.userId(1L)
			.email("user1@example.com")
			.nickname("User1")
			.build();

		User user2 = User.builder()
			.userId(2L)
			.email("user2@example.com")
			.nickname("User2")
			.build();

		Chatting message1 = Chatting.builder()
			.id(1L)
			.user(user1)
			.roomId(roomId)
			.content("Hello")
			.nickname("User1")
			.createdAt(LocalDateTime.now())
			.build();

		Chatting message2 = Chatting.builder()
			.id(2L)
			.user(user2)
			.roomId(roomId)
			.content("World")
			.nickname("User2")
			.createdAt(LocalDateTime.now())
			.build();

		List<Chatting> messages = Arrays.asList(message1, message2);
		Page<Chatting> messagePage = new PageImpl<>(messages);

		when(chattingRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)).thenReturn(messagePage);

		// Act
		Page<ChattingDto> result = chattingService.getRoomMessages(roomId, pageable);

		// Assert
		assertEquals(2, result.getContent().size());
		assertEquals("Hello", result.getContent().get(0).getContent());
		assertEquals("World", result.getContent().get(1).getContent());
		assertEquals("User1", result.getContent().get(0).getNickname());
		assertEquals("User2", result.getContent().get(1).getNickname());
	}

	@Test
	void createRoom_ShouldCreateNewRoom() {
		// Arrange
		User owner = User.builder()
			.userId(1L)
			.email("owner@example.com")
			.nickname("Owner")
			.build();

		User invitee = User.builder()
			.userId(2L)
			.email("invitee@example.com")
			.nickname("Invitee")
			.build();

		when(userRepository.findByEmail("owner@example.com")).thenReturn(owner);
		when(userRepository.findByNickname("Invitee")).thenReturn(invitee);

		ChattingRoom createdRoom = ChattingRoom.builder()
			.id(1L)
			.roomId("room-123")
			.name("Test Room")
			.owner(owner)
			.participants(Arrays.asList(owner.getUserId(), invitee.getUserId()))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		when(chattingRoomRepository.save(any(ChattingRoom.class))).thenReturn(createdRoom);

		// Act
		ChattingRoomDto result = chattingService.createRoom("Test Room", "owner@example.com", Arrays.asList("Invitee"));

		// Assert
		assertNotNull(result);
		assertEquals("Test Room", result.getName());
		assertTrue(result.getParticipants().contains(1L));
		assertTrue(result.getParticipants().contains(2L));
		verify(chattingRoomRepository).save(any(ChattingRoom.class));
	}

	@Test
	void joinRoom_ShouldAddUserToRoom() {
		// Arrange
		User user = User.builder()
			.userId(1L)
			.email("test@example.com")
			.build();

		ChattingRoom room = ChattingRoom.builder()
			.id(1L)
			.roomId("room1")
			.name("Test Room")
			.owner(User.builder().userId(2L).build())
			.participants(new ArrayList<>())
			.build();

		when(userRepository.findByEmail("test@example.com")).thenReturn(user);
		when(chattingRoomRepository.findByRoomId("room1")).thenReturn(Optional.of(room));
		when(chattingRoomRepository.save(any(ChattingRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		ChattingRoomDto result = chattingService.joinRoom("room1", "test@example.com");

		// Assert
		assertNotNull(result);
		assertEquals("room1", result.getRoomId());
		assertTrue(result.getParticipants().contains(1L));
		verify(chattingRoomRepository).save(room);
	}

	@Test
	void leaveRoom_ShouldRemoveUserFromRoom() {
		// Arrange
		User user = User.builder()
			.userId(1L)
			.email("test@example.com")
			.build();

		ChattingRoom room = ChattingRoom.builder()
			.id(1L)
			.roomId("room1")
			.name("Test Room")
			.owner(User.builder().userId(2L).build())
			.participants(new ArrayList<>(Arrays.asList(1L, 2L)))
			.build();

		when(userRepository.findByEmail("test@example.com")).thenReturn(user);
		when(chattingRoomRepository.findByRoomId("room1")).thenReturn(Optional.of(room));
		when(chattingRoomRepository.save(any(ChattingRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		ChattingRoomDto result = chattingService.leaveRoom("room1", "test@example.com");

		// Assert
		assertNotNull(result);
		assertEquals("room1", result.getRoomId());
		assertFalse(result.getParticipants().contains(1L));
		verify(chattingRoomRepository).save(room);
	}

	@Test
	void processAndSaveMessage_ShouldSaveAndReturnMessage() {
		// Arrange
		User user = User.builder()
			.userId(1L)
			.email("test@example.com")
			.nickname("TestUser")
			.build();

		ChattingRoom room = ChattingRoom.builder()
			.roomId("room1")
			.participants(Arrays.asList(1L))
			.build();

		when(userRepository.findByEmail("test@example.com")).thenReturn(user);
		when(chattingRoomRepository.findByRoomId("room1")).thenReturn(Optional.of(room));

		// Act
		ChattingDto result = chattingService.processAndSaveMessage("Hello", "room1", "test@example.com");

		// Assert
		assertNotNull(result);
		assertEquals("Hello", result.getContent());
		assertEquals("TestUser", result.getNickname());
		verify(chattingRepository).save(any(Chatting.class));
	}

	@Test
	void processAndSaveMessage_ShouldThrowExceptionWhenMessageTooLong() {
		// Arrange
		User user = User.builder()
			.userId(1L)
			.email("test@example.com")
			.nickname("TestUser")
			.build();

		ChattingRoom room = ChattingRoom.builder()
			.id(1L)
			.roomId("room1")
			.participants(Arrays.asList(1L))
			.build();

		when(userRepository.findByEmail("test@example.com")).thenReturn(user);
		when(chattingRoomRepository.findByRoomId("room1")).thenReturn(Optional.of(room));

		String longMessage = "A".repeat(1001); // 1000자를 초과하는 메시지

		// Act & Assert
		CustomException exception = assertThrows(CustomException.class, () ->
			chattingService.processAndSaveMessage(longMessage, "room1", "test@example.com"));

		assertEquals(ChattingErrorCode.MESSAGE_TOO_LONG, exception.getErrorCode());
	}
}