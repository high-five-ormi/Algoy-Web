package com.example.algoyweb.repository.chatting;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.algoyweb.model.entity.chatting.Chatting;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class ChattingRepositoryTest {

  @Autowired private ChattingRepository chattingRepository;

  @Autowired private UserRepository userRepository;

  @Test
  public void testFindByRoomIdOrderByCreatedAtDescWithPaging() {
    User user =
        User.builder()
            .username("testUser")
            .nickname("testNickname")
            .email("testuser@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();

    userRepository.save(user);

    Chatting chatting1 = Chatting.builder().content("첫 번째 메시지").roomId("12345").user(user).build();

    Chatting chatting2 = Chatting.builder().content("두 번째 메시지").roomId("12345").user(user).build();

    chattingRepository.save(chatting1);
    chattingRepository.save(chatting2);

    Pageable pageable = PageRequest.of(0, 2);

    Page<Chatting> chatMessagesPage =
        chattingRepository.findByRoomIdOrderByCreatedAtDesc("12345", pageable);

    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(2);
    assertThat(chatMessagesPage.getContent().get(0).getContent()).isEqualTo("두 번째 메시지");
    assertThat(chatMessagesPage.getContent().get(1).getContent()).isEqualTo("첫 번째 메시지");
  }

  @Test
  public void testFindByRoomIdOrderByCreatedAtDesc_NoResults() {
    Pageable pageable = PageRequest.of(0, 2);

    Page<Chatting> chatMessagesPage =
        chattingRepository.findByRoomIdOrderByCreatedAtDesc("nonExistentRoomId", pageable);

    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(0);
  }

  @Test
  public void testFindByRoomIdOrderByCreatedAtDesc_SingleResult() {
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

    Page<Chatting> chatMessagesPage =
        chattingRepository.findByRoomIdOrderByCreatedAtDesc("singleRoom", pageable);

    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(1);
    assertThat(chatMessagesPage.getContent().get(0).getContent()).isEqualTo("단일 메시지");
  }

  @Test
  public void testFindByRoomIdOrderByCreatedAtDesc_WithMultipleMessagesAndPaging() {
    User user =
        User.builder()
            .username("multiUser")
            .nickname("multiNickname")
            .email("multiuser@example.com")
            .password("password123")
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();
    userRepository.save(user);

    for (int i = 1; i <= 5; i++) {
      Chatting chatting =
          Chatting.builder().content("메시지 " + i).roomId("multiRoom").user(user).build();
      chattingRepository.save(chatting);
    }

    Pageable pageable = PageRequest.of(0, 2);

    Page<Chatting> chatMessagesPage =
        chattingRepository.findByRoomIdOrderByCreatedAtDesc("multiRoom", pageable);

    assertThat(chatMessagesPage.getTotalElements()).isEqualTo(5);
    assertThat(chatMessagesPage.getNumberOfElements()).isEqualTo(2);
    assertThat(chatMessagesPage.getContent().get(0).getContent()).isEqualTo("메시지 5");
    assertThat(chatMessagesPage.getContent().get(1).getContent()).isEqualTo("메시지 4");
  }
}