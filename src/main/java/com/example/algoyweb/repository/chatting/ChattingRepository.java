package com.example.algoyweb.repository.chatting;

import com.example.algoyweb.model.entity.chatting.Chatting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {

  Page<Chatting> findByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);
}