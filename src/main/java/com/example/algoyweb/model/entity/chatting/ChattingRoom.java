package com.example.algoyweb.model.entity.chatting;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

  @ElementCollection
  @CollectionTable(name = "chatting_room_participants", joinColumns = @JoinColumn(name = "room_id"))
  @Column(name = "user_id")
  private List<Long> participants = new ArrayList<>();

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public void addParticipant(Long userId) {
    if (participants == null) {
      participants = new ArrayList<>();
    }
    participants.add(userId);
  }

  public void removeParticipant(Long userId) {
    if (participants != null) {
      participants.remove(userId);
    }
  }
}