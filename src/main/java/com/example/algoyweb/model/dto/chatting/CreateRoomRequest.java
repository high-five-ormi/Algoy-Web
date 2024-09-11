package com.example.algoyweb.model.dto.chatting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * @author JSW
 *
 * 새로운 채팅방을 생성하기 위한 요청 정보를 담는 DTO 클래스입니다.
 * 채팅방 생성 API에서 사용됩니다.
 */
@Data
public class CreateRoomRequest {

  // 생성할 채팅방의 이름
  @NotNull(message = "채팅방 이름은 필수입니다.")
  @Size(max = 50, message = "채팅방 이름은 50자를 초과할 수 없습니다.")
  private String name;

  // 채팅방에 초대할 사용자들의 닉네임 리스트
  @Size(max = 50, message = "최대 50명까지만 초대할 수 있습니다.")
  private List<String> invitees;
}