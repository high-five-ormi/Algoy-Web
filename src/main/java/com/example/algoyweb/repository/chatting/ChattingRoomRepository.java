package com.example.algoyweb.repository.chatting;

import com.example.algoyweb.model.entity.chatting.ChattingRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author JSW
 *
 * 채팅방 정보를 조회하는 JPA 레포지토리 인터페이스입니다.
 */
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

  // 채팅방 고유 코드 (roomId)를 이용하여 채팅방 정보를 조회합니다.
  Optional<ChattingRoom> findByRoomId(String roomId);

  // 사용자 ID를 이용하여 해당 사용자가 참여하고 있는 채팅방 목록을 조회합니다.
  // 사용자가 채팅방 소유자인 경우에도 해당 채팅방이 포함됩니다.
  @Query(
      "SELECT cr FROM ChattingRoom cr WHERE :userId MEMBER OF cr.participants OR cr.owner.userId = :userId")
  List<ChattingRoom> findByUserIdInParticipantsOrOwnerId(@Param("userId") Long userId);
}