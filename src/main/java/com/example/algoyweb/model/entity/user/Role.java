package com.example.algoyweb.model.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  // ADMIN,
  // NORMAL,
  // BANNED

  // hasRole()을 사용할 때는 Spring Security가 ROLE_을 자동으로 붙인다: 각각 앞에 ROLE_을 삭제
  // ADMIN("ROLE_ADMIN", "관리자"),
  // NORMAL("ROLE_NORMAL", "일반 사용자"),
  // BANNED("ROLE_BANNED", "정지된 사용자");

  ADMIN("ADMIN", "관리자"),
  NORMAL("NORMAL", "일반 사용자"),
  BANNED("BANNED", "정지된 사용자");

  private final String key;
  private final String title;
}
