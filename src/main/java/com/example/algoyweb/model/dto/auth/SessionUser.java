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
public class SessionUser implements Serializable {
  private String username;
  private String nickname;
  private String email;

  // SessionUser 객체 생성
  public SessionUser(User user) {
    this.username = user.getUsername();
    this.nickname = user.getNickname();
    this.email = user.getEmail();
  }
}
