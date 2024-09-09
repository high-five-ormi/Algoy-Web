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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChattingRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String roomId;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;

  @ElementCollection
  @CollectionTable(name = "chatting_room_participants", joinColumns = @JoinColumn(name = "room_id"))
  @Column(name = "user_id")
  private List<Long> participants = new ArrayList<>();

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public void addParticipant(Long userId) {
    if (!participants.contains(userId)) {
      participants.add(userId);
    }
  }

  public void removeParticipant(Long userId) {
    participants.remove(userId);
  }

  public void changeOwner(User newOwner) {
    if (!this.participants.contains(newOwner.getUserId())) {
      throw new IllegalArgumentException("New owner must be a participant of the room");
    }
    this.owner = newOwner;
  }

  public boolean isParticipant(Long userId) {
    return this.participants.contains(userId);
  }

  public boolean isEmpty() {
    return this.participants.isEmpty();
  }
}