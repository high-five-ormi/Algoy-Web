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

@DataJpaTest
class ChattingRoomRepositoryTest {

  @Autowired private ChattingRoomRepository chattingRoomRepository;

  @Autowired private UserRepository userRepository;

  @Test
  public void testFindByRoomId() {
    // 방 소유자 생성
    User owner =
        User.builder()
            .username("owner")
            .nickname("ownerNickname")
            .email("owner@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(owner);

    // 채팅 방 생성 및 저장
    ChattingRoom chattingRoom =
        ChattingRoom.builder()
            .roomId("room1")
            .name("첫 번째 방")
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .build();
    chattingRoomRepository.save(chattingRoom);

    // 방 ID로 조회
    Optional<ChattingRoom> foundRoom = chattingRoomRepository.findByRoomId("room1");

    // 방이 존재하는지 확인
    assertThat(foundRoom).isPresent();
    assertThat(foundRoom.get().getName()).isEqualTo("첫 번째 방");
  }

  @Test
  public void testFindByUserIdInParticipantsOrOwnerId() {
    // 방 소유자 생성
    User owner =
        User.builder()
            .username("owner")
            .nickname("ownerNickname")
            .email("owner@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(owner);

    // 참여자 생성
    User participant =
        User.builder()
            .username("participant")
            .nickname("participantNickname")
            .email("participant@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(participant);

    ChattingRoom chattingRoom =
        ChattingRoom.builder()
            .roomId("room2")
            .name("두 번째 방")
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .build();
    chattingRoom.addParticipant(participant.getUserId());
    chattingRoomRepository.save(chattingRoom);

    List<ChattingRoom> rooms =
        chattingRoomRepository.findByUserIdInParticipantsOrOwnerId(participant.getUserId());

    assertThat(rooms.size()).isEqualTo(1);
    assertThat(rooms.get(0).getName()).isEqualTo("두 번째 방");
  }

  @Test
  public void testAddAndRemoveParticipant() {
    // 방 소유자 생성
    User owner =
        User.builder()
            .username("owner")
            .nickname("ownerNickname")
            .email("owner@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(owner);

    // 참여자 생성
    User participant =
        User.builder()
            .username("participant")
            .nickname("participantNickname")
            .email("participant@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(participant);

    // 채팅 방 생성 및 저장
    ChattingRoom chattingRoom =
        ChattingRoom.builder()
            .roomId("room3")
            .name("세 번째 방")
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .build();
    chattingRoomRepository.save(chattingRoom);

    // 참여자 추가
    chattingRoom.addParticipant(participant.getUserId());
    chattingRoomRepository.save(chattingRoom);

    // 참여자가 추가되었는지 확인
    Optional<ChattingRoom> foundRoom = chattingRoomRepository.findByRoomId("room3");
    assertThat(foundRoom).isPresent();
    assertThat(foundRoom.get().isParticipant(participant.getUserId())).isTrue();

    // 참여자 삭제
    chattingRoom.removeParticipant(participant.getUserId());
    chattingRoomRepository.save(chattingRoom);

    // 참여자가 삭제되었는지 확인
    Optional<ChattingRoom> updatedRoom = chattingRoomRepository.findByRoomId("room3");
    assertThat(updatedRoom).isPresent();
    assertThat(updatedRoom.get().isParticipant(participant.getUserId())).isFalse();
  }

  @Test
  public void testChangeOwner() {
    // 방 소유자 생성
    User owner =
        User.builder()
            .username("owner")
            .nickname("ownerNickname")
            .email("owner@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(owner);

    // 새로운 소유자 생성
    User newOwner =
        User.builder()
            .username("newOwner")
            .nickname("newOwnerNickname")
            .email("newOwner@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(newOwner);

    // 채팅 방 생성 및 저장
    ChattingRoom chattingRoom =
        ChattingRoom.builder()
            .roomId("room4")
            .name("네 번째 방")
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .build();
    chattingRoom.addParticipant(newOwner.getUserId()); // 새로운 소유자를 참여자로 추가
    chattingRoomRepository.save(chattingRoom);

    // 소유자 변경
    chattingRoom.changeOwner(newOwner);
    chattingRoomRepository.save(chattingRoom);

    // 소유자가 변경되었는지 확인
    Optional<ChattingRoom> updatedRoom = chattingRoomRepository.findByRoomId("room4");
    assertThat(updatedRoom).isPresent();
    assertThat(updatedRoom.get().getOwner().getUserId()).isEqualTo(newOwner.getUserId());
  }

  @Test
  public void testRoomIsEmpty() {
    // 방 소유자 생성
    User owner =
        User.builder()
            .username("owner")
            .nickname("ownerNickname")
            .email("owner@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(owner);

    // 채팅 방 생성 및 저장
    ChattingRoom chattingRoom =
        ChattingRoom.builder()
            .roomId("room5")
            .name("다섯 번째 방")
            .owner(owner)
            .createdAt(LocalDateTime.now())
            .build();
    chattingRoomRepository.save(chattingRoom);

    // 방이 비어 있는지 확인
    Optional<ChattingRoom> foundRoom = chattingRoomRepository.findByRoomId("room5");
    assertThat(foundRoom).isPresent();
    assertThat(foundRoom.get().isEmpty()).isTrue();
  }
}
