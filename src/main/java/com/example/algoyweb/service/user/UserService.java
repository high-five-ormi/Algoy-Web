package com.example.algoyweb.service.user;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; // Spring Security의 PasswordEncoder 사용

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * 회원가입 처리
   *
   * @author yuseok
   * @param userDto 회원가입 정보를 담고 있는 DTO
   * @return 저장된 사용자 정보를 담은 UserDto
   */
  @Transactional
  public UserDto signUpUser(UserDto userDto) {
    // 닉네임 중복 체크
    if (checkNicknameDuplicate(userDto.getNickname())) {
      throw new IllegalArgumentException("사용 중인 닉네임입니다. 다시 입력하세요.");
    }

    // 이메일 중복 체크
    if (checkEmailDuplicate(userDto.getEmail())) {
      throw new IllegalArgumentException("사용 중인 이메일입니다. 다시 입력하세요.");
    }

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(userDto.getPassword());

    // User 엔티티 생성
    User user =
        User.builder()
            .username(userDto.getUsername())
            .nickname(userDto.getNickname())
            .email(userDto.getEmail())
            .password(encodedPassword)
            .role(userDto.getRole())
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();

    // 저장
    userRepository.save(user);

    // 저장된 엔티티를 DTO로 반환
    return UserDto.toDto(user);
  }

  // 닉네임 중복 체크
  public boolean checkNicknameDuplicate(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  // 이메일 중복 체크
  public boolean checkEmailDuplicate(String email) {
    return userRepository.existsByEmail(email);
  }
}
