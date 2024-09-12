package com.example.algoyweb.repository.chatting;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author JSW
 *
 * ChattingRepository의 단위 테스트
 */
@DataJpaTest
class ChattingRepositoryTest {

  @Autowired private ChattingRepository chattingRepository;

  @Autowired private UserRepository userRepository;

  /**
   * 채팅방 ID로 메시지를 조회하고 생성 시간 역순으로 정렬하는 기능을 테스트합니다.
   * 페이징 처리도 함께 테스트합니다.
   */
  @Test
  public void testFindByRoomIdOrderByCreatedAtDescWithPaging() {
    // Given
    User user = User.builder()
        .username("testUser")
        .nickname("testNickname")
        .email("testuser@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();

    userRepository.save(user);

    Chatting chatting1 = Chatting.builder()
        .content("첫 번째 메시지")
        .roomId("12345")
        .user(user)
        .nickname(user.getNickname())
        .createdAt(LocalDateTime.now().minusMinutes(1))
        .build();

    Chatting chatting2 = Chatting.builder()
        .content("두 번째 메시지")
        .roomId("12345")
        .user(user)
        .nickname(user.getNickname())
        .createdAt(LocalDateTime.now())
        .build();

    Chatting systemMessage = Chatting.builder()
        .content("시스템 메시지")
        .roomId("12345")
        .user(null)
        .nickname("System")
        .createdAt(LocalDateTime.now().plusMinutes(1))
        .build();

    chattingRepository.saveAll(Arrays.asList(chatting1, chatting2, systemMessage));

    Pageable pageable = PageRequest.of(0, 3);

    // When
    Page<Chatting> chatMessagesPage = chattingRepository.findByRoomIdOrderByCreatedAtDesc("12345", pageable);

    // Then
    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(3);
    assertThat(chatMessagesPage.getContent().get(0).getContent()).isEqualTo("시스템 메시지");
    assertThat(chatMessagesPage.getContent().get(0).getNickname()).isEqualTo("System");
    assertThat(chatMessagesPage.getContent().get(1).getContent()).isEqualTo("두 번째 메시지");
    assertThat(chatMessagesPage.getContent().get(2).getContent()).isEqualTo("첫 번째 메시지");

    // User가 null인 경우 (시스템 메시지) 확인
    assertThat(chatMessagesPage.getContent().get(0).getUser()).isNull();
    // User가 설정된 경우 확인
    assertThat(chatMessagesPage.getContent().get(1).getUser()).isNotNull();
    assertThat(chatMessagesPage.getContent().get(1).getUser().getNickname()).isEqualTo("testNickname");
  }

  /**
   * 존재하지 않는 채팅방 ID로 메시지를 조회할 때의 동작을 테스트합니다.
   */
  @Test
  public void testFindByRoomIdOrderByCreatedAtDesc_NoResults() {
    // Given
    Pageable pageable = PageRequest.of(0, 2);

    // When
    Page<Chatting> chatMessagesPage =
        chattingRepository.findByRoomIdOrderByCreatedAtDesc("nonExistentRoomId", pageable);

    // Then
    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(0);
  }

  /**
   * 채팅방에 단일 메시지만 있을 때의 조회 기능을 테스트합니다.
   */
  @Test
  public void testFindByRoomIdOrderByCreatedAtDesc_SingleResult() {
    // Given
    User user =
        User.builder()
            .username("singleUser")
            .nickname("singleNickname")
            .email("singleuser@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(user);

    Chatting chatting =
        Chatting.builder().content("단일 메시지").roomId("singleRoom").user(user).build();
    chattingRepository.save(chatting);

    Pageable pageable = PageRequest.of(0, 2);

    // When
    Page<Chatting> chatMessagesPage =
        chattingRepository.findByRoomIdOrderByCreatedAtDesc("singleRoom", pageable);

    // Then
    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(1);
    assertThat(chatMessagesPage.getContent().get(0).getContent()).isEqualTo("단일 메시지");
  }

  /**
   * 여러 메시지가 있는 채팅방에서의 페이징 처리를 테스트합니다.
   */
  @Test
  public void testFindByRoomIdOrderByCreatedAtDesc_WithMultipleMessagesAndPaging() {
    // Given
    User user = User.builder()
        .username("multiUser")
        .nickname("multiNickname")
        .email("multiuser@example.com")
        .password("password123")
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
    userRepository.save(user);

    String roomId = "multiRoom";
    for (int i = 1; i <= 5; i++) {
      Chatting chatting = Chatting.builder()
          .content("메시지 " + i)
          .roomId(roomId)
          .user(user)
          .createdAt(LocalDateTime.now())
          .build();
      chattingRepository.save(chatting);
    }

    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

    // When
    Page<Chatting> chatMessagesPage = chattingRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);

    // Then
    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(5);
    assertThat(chatMessagesPage.getNumberOfElements()).isEqualTo(2);
    assertThat(chatMessagesPage.getContent()).hasSize(2);
    assertThat(chatMessagesPage.getContent().get(0).getContent()).isEqualTo("메시지 5");
    assertThat(chatMessagesPage.getContent().get(1).getContent()).isEqualTo("메시지 4");
  }
}