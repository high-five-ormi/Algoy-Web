package com.example.algoyweb.model.entity.chatting;

import com.example.algoyweb.model.entity.user.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author JSW
 *
 * 채팅방 정보를 저장하는 엔티티 클래스입니다.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChattingRoom {

  // 채팅방 고유 식별자(PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 채팅방 고유 코드
  @Column(nullable = false, unique = true)
  private String roomId;

  // 채팅방 이름
  @Column(nullable = false)
  private String name;

  // 채팅방 생성자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;

  // 채팅방 참여자 목록
  @Builder.Default
  @ElementCollection
  @CollectionTable(name = "chatting_room_participants", joinColumns = @JoinColumn(name = "room_id"))
  @Column(name = "user_id")
  private List<Long> participants = new ArrayList<>();

  // 채팅방 생성 시간
  private LocalDateTime createdAt;

  // 채팅방 수정 시간
  private LocalDateTime updatedAt;

  /**
   * 참여자 제거 메소드
   *
   * @param userId 제거할 사용자 ID
   */
  public void addParticipant(Long userId) {
    if (!participants.contains(userId)) {
      participants.add(userId);
    }
  }

  /**
   * 채팅방 참여자를 제거하는 메소드입니다.
   *
   * @param userId 제거할 사용자의 ID
   */
  public void removeParticipant(Long userId) {
    participants.remove(userId);
  }

  /**
   * 채팅방 소유자를 변경하는 메소드입니다.
   *
   * @param newOwner 새로운 소유자 정보
   */
  public void changeOwner(User newOwner) {
    if (!this.participants.contains(newOwner.getUserId())) {
      throw new IllegalArgumentException("New owner must be a participant of the room");
    }
    this.owner = newOwner;
  }

  /**
   * 사용자가 채팅방 참여자인지 확인하는 메소드
   *
   * @param userId 확인할 사용자 ID
   * @return 사용자가 참여자이면 true, 아니면 false
   */
  public boolean isParticipant(Long userId) {
    return this.participants.contains(userId);
  }

  /**
   * 채팅방 참여자가 있는지 확인하는 메소드
   *
   * @return 참여자가 없으면 true, 아니면 false
   */
  public boolean isEmpty() {
    return this.participants.isEmpty();
  }
}