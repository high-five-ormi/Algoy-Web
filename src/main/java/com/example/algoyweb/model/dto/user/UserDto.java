package com.example.algoyweb.model.dto.user;

import java.time.LocalDateTime;

import com.example.algoyweb.model.entity.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserDto {
	private Long userId;
	private String username;
	private String nickname;
	private String email;
	private String password;
	private String solvedacUserName; // solvedAC username 입력받음
	private Role role;
	private Boolean isDeleted;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
	private Integer banCount;
	private LocalDateTime banExpiration;

}