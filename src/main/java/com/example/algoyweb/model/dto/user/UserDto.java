package com.example.algoyweb.model.dto.user;

import java.time.LocalDateTime;

import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.model.entity.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private Long userId;
  private String username;
  private String nickname;
  private String email;
  private String password;
  private Role role;
  private boolean isDeleted;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  public static UserDto toDto(User user) {
    UserDto userDto =
        UserDto.builder()
            .userId(user.getUserId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .password(user.getPassword())
            .role(user.getRole())
            .isDeleted(user.isDeleted())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .deletedAt(user.getDeletedAt())
            .build();

    return userDto;
  }

  public static User toEntity(UserDto userDto) {
    User user =
        User.builder()
            .userId(userDto.getUserId())
            .username(userDto.getUsername())
            .nickname(userDto.getNickname())
            .email(userDto.getEmail())
            .password(userDto.getPassword())
            .role(userDto.getRole())
            .isDeleted(userDto.isDeleted())
            .createdAt(userDto.getCreatedAt())
            .updatedAt(userDto.getUpdatedAt())
            .deletedAt(userDto.getDeletedAt())
            .build();

    return user;
  }
}
