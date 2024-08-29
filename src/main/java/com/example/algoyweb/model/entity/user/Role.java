package com.example.algoyweb.model.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  // ADMIN,
  // NORMAL,
  // BANNED

  ADMIN("ROLE_ADMIN", "관리자"),
  NORMAL("ROLE_NORMAL", "일반 사용자"),
  BANNED("ROLE_BANNED", "정지된 사용자");

  private final String key;
  private final String title;
}
