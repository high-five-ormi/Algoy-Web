package com.example.algoyweb.service.user;

import java.time.LocalDateTime;
import java.util.List;

// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.Role;
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
    // User 엔티티 생성
    User user =
        User.builder()
            .username(userDto.getUsername())
            .nickname(userDto.getNickname())
            .email(userDto.getEmail())
            .password(passwordEncoder.encode(userDto.getPassword())) // 비밀번호 암호화
            .role(Role.NORMAL)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .build();

    // 저장
    userRepository.save(user);

    // 저장된 엔티티를 DTO로 반환
    return UserDto.toDto(user);
  }

  // 닉네임 중복 체크
  @Transactional
  public User findByNickname(String nickname) {
    return userRepository.findByNickname(nickname);
  }

  // 이메일 중복 체크
  @Transactional
  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
