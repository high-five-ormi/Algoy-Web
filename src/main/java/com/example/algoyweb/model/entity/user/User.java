package com.example.algoyweb.model.entity.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.planner.Planner;

import com.example.algoyweb.model.entity.study.Comment;
import com.example.algoyweb.model.entity.study.Study;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "user_name", nullable = false)
	private String username;

	@Column(name = "nick_name", nullable = false)
	private String nickname;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "solvedac_username", nullable = true) // solvedAC username의 기본값은 null이다.
	private String solvedacUserName;

	@Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "ban_count", nullable = false)
	private int banCount = 0; // 정지 횟수: 초기 값은 0

	@Column(name = "ban_reason")
	private String banReason; // 정지 사유

	@Column(name = "ban_expiration")
	private LocalDateTime banExpiration; // 정지 유효시간 (만료 시간)

	@OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Planner> plannerList;

	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private List<Study> studyList;

	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private List<Comment> commentList;

	public void connectPlanner(Planner planner) {
		if (this.plannerList == null)
			this.plannerList = new ArrayList<>();
		this.getPlannerList().add(planner);
	}

	public void connectStudy(Study study) {
		if (this.studyList == null)
			this.studyList = new ArrayList<>();
		this.getStudyList().add(study);
	}

	public void connectComment(Comment comment) {
		if (this.commentList == null)
			this.commentList = new ArrayList<>();
		this.getCommentList().add(comment);
	}

	public void update(String username, String nickname, String email, String password, Role role, Boolean isDeleted) {
		this.username = username;
		this.nickname = nickname;
		this.email = email;
		this.password = password;
		this.role = role;
		this.isDeleted = isDeleted;
		this.updatedAt = LocalDateTime.now();
	}

	public void setDeleted() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now().plusMonths(1);
	}

	public void restore() {
		this.isDeleted = false;
		this.deletedAt = null;
	}

	public String getRoleKey() {
		return this.role.getKey();
	}

	public void updateUser(UserDto userDto, String encodedPassword) {
		if (userDto.getNickname() != null) {
			this.nickname = userDto.getNickname();
		}
		if (encodedPassword != null) {
			this.password = encodedPassword;
		}
		if (userDto.getSolvedacUserName() != null) {  // solvedacUsername 업데이트 추가
			this.solvedacUserName = userDto.getSolvedacUserName();
		}
		if (userDto.getIsDeleted() != null) {
			this.isDeleted = userDto.getIsDeleted();
		}
		this.updatedAt = LocalDateTime.now();
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateRole(Role role) {
		this.role = role;
		this.updatedAt = LocalDateTime.now();
	}

	public boolean isBanned() {
		return role == Role.BANNED && banExpiration != null && LocalDateTime.now().isBefore(banExpiration);
	}

	public void increaseBanCount() {
		this.banCount++;
	}

	public void updateBanExpiration(LocalDateTime expirationTime) {
		this.banExpiration = expirationTime;
	}

	public void updateBanReason(String banReason) {
		this.banReason = banReason;
	}
}