package com.example.algoyweb.repository.chatting;

import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

  Optional<ChattingRoom> findByRoomId(String roomId);
}