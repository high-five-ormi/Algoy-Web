package com.example.algoyweb.model.entity.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.example.algoyweb.model.entity.user.Role.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
  private User user;

  @BeforeEach
  public void setUp() {
    user = User.builder()
		.username("test username")
		.nickname("test nickname")
		.email("test@example.com")
		.password("password123")
		.role(NORMAL)
		.isDeleted(false)
		.build();
  }

  @Test
  public void testUserCreation() {
	assertThat(user.getUsername()).isEqualTo("test username");
	assertThat(user.getNickname()).isEqualTo("test nickname");
	assertThat(user.getEmail()).isEqualTo("test@example.com");
	assertThat(user.getPassword()).isEqualTo("password123");
	assertThat(user.getRole()).isEqualTo(NORMAL);
	assertThat(user.getIsDeleted()).isEqualTo(false);
  }
}