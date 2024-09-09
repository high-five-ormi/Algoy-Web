package com.example.algoyweb.repository.chatting;

import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

  Optional<ChattingRoom> findByRoomId(String roomId);

  @Query("SELECT cr FROM ChattingRoom cr WHERE :userId MEMBER OF cr.participants OR cr.owner.userId = :userId")
  List<ChattingRoom> findByUserIdInParticipantsOrOwnerId(@Param("userId") Long userId);
}