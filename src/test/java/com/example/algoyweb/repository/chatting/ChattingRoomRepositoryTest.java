package com.example.algoyweb.repository.chatting;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author JSW
 *
 * ChattingRoomRepository의 단위 테스트
 */
@DataJpaTest
class ChattingRoomRepositoryTest {

  @Autowired private ChattingRoomRepository chattingRoomRepository;

  @Autowired private UserRepository userRepository;

  /**
   * 채팅방 ID로 채팅방을 조회하는 기능을 테스트합니다.
   */
  @Test
  public void testFindByRoomId() {
    // Given
    User owner = User.builder()
        .username("owner")
        .nickname("ownerNickname")
        .email("owner@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(owner);

    ChattingRoom chattingRoom = ChattingRoom.builder()
        .roomId("room1")
        .name("첫 번째 방")
        .owner(owner)
        .createdAt(LocalDateTime.now())
        .build();
    chattingRoomRepository.save(chattingRoom);

    // When
    Optional<ChattingRoom> foundRoom = chattingRoomRepository.findByRoomId("room1");

    // Then
    assertThat(foundRoom).isPresent();
    assertThat(foundRoom.get().getName()).isEqualTo("첫 번째 방");
  }

  /**
   * 사용자 ID로 참여 중인 채팅방을 조회하는 기능을 테스트합니다.
   */
  @Test
  public void testFindByUserIdInParticipantsOrOwnerId() {
    // Given
    User owner = User.builder()
        .username("owner")
        .nickname("ownerNickname")
        .email("owner@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(owner);

    User participant = User.builder()
        .username("participant")
        .nickname("participantNickname")
        .email("participant@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(participant);

    ChattingRoom chattingRoom = ChattingRoom.builder()
        .roomId("room2")
        .name("두 번째 방")
        .owner(owner)
        .createdAt(LocalDateTime.now())
        .build();
    chattingRoom.addParticipant(participant.getUserId());
    chattingRoomRepository.save(chattingRoom);

    // When
    List<ChattingRoom> rooms = chattingRoomRepository.findByUserIdInParticipantsOrOwnerId(participant.getUserId());

    // Then
    assertThat(rooms.size()).isEqualTo(1);
    assertThat(rooms.get(0).getName()).isEqualTo("두 번째 방");
  }

  /**
   * 채팅방에 참여자를 추가하고 제거하는 기능을 테스트합니다.
   */
  @Test
  public void testAddAndRemoveParticipant() {
    // Given
    User owner = User.builder()
        .username("owner")
        .nickname("ownerNickname")
        .email("owner@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(owner);

    User participant = User.builder()
        .username("participant")
        .nickname("participantNickname")
        .email("participant@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(participant);

    ChattingRoom chattingRoom = ChattingRoom.builder()
        .roomId("room3")
        .name("세 번째 방")
        .owner(owner)
        .createdAt(LocalDateTime.now())
        .build();
    chattingRoomRepository.save(chattingRoom);

    // When
    chattingRoom.addParticipant(participant.getUserId());
    chattingRoomRepository.save(chattingRoom);

    // Then
    Optional<ChattingRoom> foundRoom = chattingRoomRepository.findByRoomId("room3");
    assertThat(foundRoom).isPresent();
    assertThat(foundRoom.get().isParticipant(participant.getUserId())).isTrue();

    // When
    chattingRoom.removeParticipant(participant.getUserId());
    chattingRoomRepository.save(chattingRoom);

    // Then
    Optional<ChattingRoom> updatedRoom = chattingRoomRepository.findByRoomId("room3");
    assertThat(updatedRoom).isPresent();
    assertThat(updatedRoom.get().isParticipant(participant.getUserId())).isFalse();
  }

  /**
   * 채팅방의 소유자를 변경하는 기능을 테스트합니다.
   */
  @Test
  public void testChangeOwner() {
    // Given
    User owner = User.builder()
        .username("owner")
        .nickname("ownerNickname")
        .email("owner@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(owner);

    User newOwner = User.builder()
        .username("newOwner")
        .nickname("newOwnerNickname")
        .email("newOwner@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(newOwner);

    ChattingRoom chattingRoom = ChattingRoom.builder()
        .roomId("room4")
        .name("네 번째 방")
        .owner(owner)
        .createdAt(LocalDateTime.now())
        .build();
    chattingRoom.addParticipant(newOwner.getUserId());
    chattingRoomRepository.save(chattingRoom);

    // When
    chattingRoom.changeOwner(newOwner);
    chattingRoomRepository.save(chattingRoom);

    // Then
    Optional<ChattingRoom> updatedRoom = chattingRoomRepository.findByRoomId("room4");
    assertThat(updatedRoom).isPresent();
    assertThat(updatedRoom.get().getOwner().getUserId()).isEqualTo(newOwner.getUserId());
  }

  /**
   * 채팅방이 비어있는지 확인하는 기능을 테스트합니다.
   */
  @Test
  public void testRoomIsEmpty() {
    // Given
    User owner = User.builder()
        .username("owner")
        .nickname("ownerNickname")
        .email("owner@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(owner);

    ChattingRoom chattingRoom = ChattingRoom.builder()
        .roomId("room5")
        .name("다섯 번째 방")
        .owner(owner)
        .createdAt(LocalDateTime.now())
        .build();
    chattingRoomRepository.save(chattingRoom);

    // When
    Optional<ChattingRoom> foundRoom = chattingRoomRepository.findByRoomId("room5");

    // Then
    assertThat(foundRoom).isPresent();
    assertThat(foundRoom.get().isEmpty()).isTrue();
  }
}