package com.example.algoyweb.repository.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.example.algoyweb.model.entity.user.User;

import static com.example.algoyweb.model.entity.user.Role.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
public class UserRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

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
			.createdAt(LocalDateTime.now())
			.build();
	}

	@Test
	public void testFindByEmail() {
		entityManager.persist(user);
		entityManager.flush();

		User found = userRepository.findByEmail(user.getEmail());

		assertThat(found.getUsername()).isEqualTo(user.getUsername());
		assertThat(found.getNickname()).isEqualTo(user.getNickname());
		assertThat(found.getEmail()).isEqualTo(user.getEmail());
		assertThat(found.getPassword()).isEqualTo(user.getPassword());
	}

	@Test
	public void testFindByNickname() {
		entityManager.persist(user);
		entityManager.flush();

		User found = userRepository.findByNickname(user.getNickname());

		assertThat(found.getUsername()).isEqualTo(user.getUsername());
		assertThat(found.getNickname()).isEqualTo(user.getNickname());
		assertThat(found.getEmail()).isEqualTo(user.getEmail());
		assertThat(found.getPassword()).isEqualTo(user.getPassword());
	}
}