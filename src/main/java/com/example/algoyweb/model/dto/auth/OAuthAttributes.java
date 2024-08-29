package com.example.algoyweb.model.dto.auth;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;

import lombok.Builder;
import lombok.Getter;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author yuseok
 *
 * OAuth2 인증을 위한 사용자 속성을 저장하는 클래스
 */
@Getter
public class OAuthAttributes {
  private Map<String, Object> attributes; // 사용자 속성 맵
  private String nameAttributeKey; // 이름 속성 키
  private String username;
  private String nickname;
  private String email;
  private String password;

  /**
   * @author yuseok
   *
   * @param attributes 사용자의 속성 값을 담은 맵
   * @param nameAttributeKey 이름 속성의 키 값
   * @param username 사용자 이름
   * @param nickname 사용자의 닉네임
   * @param email 사용자의 이메일 주소
   * @param password 사용자의 비밀번호
   *
   * @return 생성된 OAuthAttributes 객체
   */
  @Builder
  public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String username, String nickname, String email, String password) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.username = username;
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  // OAuth2User에서 반환하는 사용자 정보는 Map이기에 값 하나하나를 변환해야 한다.
  public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
    return ofGoogle(userNameAttributeName, attributes); // 구글 사용자 속성 생성 메서드 호출
  }

  // 구글 생성자
  private static OAuthAttributes ofGoogle(String usernameAttributeName, Map<String, Object> attributes) {
    // 닉네임과 패스워드는 직접 입력해야 함: Null Point Exception 방지
    String email = (String) attributes.get("email");
    String nickname = (String) attributes.get("nickname");

    if (nickname == null) {
      // @앞 부분을 닉네임으로 지정, 그러리가 없겠지만 이메일이 없다면 기본 닉네임 DefaultNickname으로 지정
      nickname = email != null ? email.split("@")[0] : "DefaultNickname";
    }

    // 암호화된 패스워드 생성
    String randomPassword = createRandomPassword();

    return OAuthAttributes.builder()
        .username((String) attributes.get("name"))
        .nickname(nickname)
        .email(email)
        .password(randomPassword) // 비밀번호 설정
        .attributes(attributes)
        .nameAttributeKey(usernameAttributeName)
        .build();
  }

  // 비밀번호 랜덤 생성 및 암호화
  private static String createRandomPassword() {
    String rawPassword = RandomStringUtils.randomAlphanumeric(16); // 알파벳과 숫자로 이뤄진 16자리 비밀번호 생성
    return new BCryptPasswordEncoder().encode(rawPassword); // 비밀번호 안전하게 인코딩
  }

  // User 엔티티 생성
  public User toEntity() {
    return User.builder()
        .username(username)
        .nickname(nickname)
        .email(email)
        .password(password)
        .role(Role.NORMAL)
        .isDeleted(false)
        .createdAt(LocalDateTime.now())
        .build();
  }
}