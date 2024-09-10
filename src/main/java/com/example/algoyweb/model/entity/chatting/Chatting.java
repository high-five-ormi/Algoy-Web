package com.example.algoyweb.model.entity.chatting;

import com.example.algoyweb.model.entity.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Chatting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 메시지의 고유 식별자입니다.

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String roomId;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.nickname = user.getNickname();
  }
}