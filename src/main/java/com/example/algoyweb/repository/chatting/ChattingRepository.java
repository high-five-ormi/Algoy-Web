package com.example.algoyweb.repository.chatting;

import com.example.algoyweb.model.entity.chatting.Chatting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author JSW
 *
 * 채팅 메시지 데이터를 조회하는 JPA 레포지토리 인터페이스입니다.
 */
public interface ChattingRepository extends JpaRepository<Chatting, Long> {

  // 특정 채팅방의 채팅 메시지 목록을 최신순으로 페이징하여 조회합니다.
  @Query(
      "SELECT c FROM Chatting c LEFT JOIN FETCH c.user WHERE c.roomId = :roomId ORDER BY c.createdAt DESC")
  Page<Chatting> findByRoomIdOrderByCreatedAtDesc(
      @Param("roomId") String roomId, Pageable pageable);
}