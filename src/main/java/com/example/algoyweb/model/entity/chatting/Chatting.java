package com.example.algoyweb.model.entity.chatting;

import com.example.algoyweb.model.entity.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author JSW
 *
 * 채팅 메시지를 저장하는 엔티티 클래스입니다.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Chatting {

  // 채팅 메시지 고유 식별자(PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 메시지를 작성한 사용자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  // 채팅방 ID(FK)
  @Column(nullable = false)
  private String roomId;

  // 채팅 메시지 내용
  @Column(nullable = false)
  private String content;

  // 메시지 작성자 닉네임
  @Column(nullable = false)
  private String nickname;

  // 메시지 생성 시간
  @Column(nullable = false)
  private LocalDateTime createdAt;

  // 메시지 생성 시 자동으로 현재 시간 설정하는 메소드
  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    if (this.user != null) {
      this.nickname = user.getNickname();
    }
  }
}