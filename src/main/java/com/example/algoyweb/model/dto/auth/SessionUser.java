package com.example.algoyweb.model.dto.auth;

import java.io.Serializable;

import com.example.algoyweb.model.entity.user.User;

import lombok.Getter;

/**
 * 세션에 저장되는 사용자 정보를 나타내는 클래스
 *
 * @author yuseok
 */
@Getter
public class SessionUser implements Serializable { // 직렬화 기능을 가진 세션 DTO
  // 인증된 사용자 정보만 필요 => name, email 필드만 선언
  private String username;
  private String email;

  // SessionUser 객체 생성
  public SessionUser(User user) {
    this.username = user.getUsername();
    this.email = user.getEmail();
  }
}
