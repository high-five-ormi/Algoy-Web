package com.example.algoyweb.model.dto.user;

import java.time.LocalDateTime;

import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private Long userId;
  private String username;
  private String nickname;
  private String email;
  private String password;
  private Role role;
  private Boolean isDeleted;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  public static UserDto toDto(User user) {
	  return UserDto.builder()
		  .userId(user.getUserId())
		  .username(user.getUsername())
		  .nickname(user.getNickname())
		  .email(user.getEmail())
		  .password(user.getPassword())
		  .role(user.getRole())
		  .isDeleted(user.getIsDeleted())
		  .createdAt(user.getCreatedAt())
		  .updatedAt(user.getUpdatedAt())
		  .deletedAt(user.getDeletedAt())
		  .build();
  }

  public User toEntity() {
	  return User.builder()
		  .userId(this.getUserId())
		  .username(this.getUsername())
		  .nickname(this.getNickname())
		  .email(this.getEmail())
		  .password(this.getPassword())
		  .role(this.getRole())
		  .isDeleted(this.getIsDeleted())
		  .createdAt(this.getCreatedAt())
		  .updatedAt(this.getUpdatedAt())
		  .deletedAt(this.getDeletedAt())
		  .build();
  }
}