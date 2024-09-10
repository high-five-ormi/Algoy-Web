package com.example.algoyweb.service.user;

import java.time.LocalDateTime;
import java.util.*;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.exception.errorcode.UserErrorCode;
import com.example.algoyweb.model.entity.allen.SolvedACResponseEntity;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.repository.allen.SolvedACResponseRepository;
import com.example.algoyweb.util.ConvertUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;

import org.springframework.web.client.RestTemplate;

@Service
public class UserService implements UserDetailsService {
	private final SolvedACResponseRepository solvedACResponseRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder; // Spring Security의 PasswordEncoder 사용

	@Autowired
	public UserService(SolvedACResponseRepository solvedACResponseRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.solvedACResponseRepository = solvedACResponseRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * 회원가입 처리
	 *
	 * @author yuseok
	 * @param userDto 회원가입 정보를 담고 있는 DTO
	 *
	 * @author 조아라
	 * solvedAC username db 저장하는 로직 추가
	 * @since 24. 09. 08
	 */
	@Transactional
	public void signUpUser(UserDto userDto) {
		// SolvedAC username 유효성 확인 되면 db에 저장하기 위한 기능
		if (userDto.getSolvedacUserName() != null && !userDto.getSolvedacUserName().isEmpty()) {
			boolean isValid = isUsernameValid(userDto.getSolvedacUserName());
			if (!isValid) {
				throw new CustomException(UserErrorCode.INVALID_SOLVEDAC_USERNAME);
			}
		}

		// User 엔티티 생성
		User user = User.builder()
			.username(userDto.getUsername())
			.nickname(userDto.getNickname())
			.email(userDto.getEmail())
			.password(passwordEncoder.encode(userDto.getPassword())) // 비밀번호 암호화
			.solvedacUserName(userDto.getSolvedacUserName()) // solvedAC username 저장(유효성을 확인하는 로직 필요)
			.role(Role.NORMAL)
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.banCount(0)
			.build();

		// 저장
		userRepository.save(user);
	}

	// 이메일로 사용자 찾기
	@Transactional
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	// 닉네임으로 사용자 찾기
	@Transactional
	public User findByNickname(String nickname) {
		return userRepository.findByNickname(nickname);
	}

  /**
   * @author JSW
   *
   * 주어진 사용자 이름(이메일)을 기준으로 사용자 정보를 가져옵니다.
   *
   * @param username 검색할 사용자의 사용자 이름(이메일)
   * @return UserDto 사용자 정보를 담은 DTO 객체
   */
  @Transactional(readOnly = true)
  public UserDto getUserByUsername(String username) {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new RuntimeException("User not found");
		}
		return ConvertUtils.convertUserToDto(user);
	}

	/**
	 * 로그인
	 *
	 * @param email 로그인시 email로 로그인
	 * @return 저장된 사용자 정보를 담은 UserDto
	 * @author jooyoung
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + email);
		}

		return org.springframework.security.core.userdetails.User
			.withUsername(user.getEmail())
			.password(user.getPassword())  // Assuming this is already hashed
			.authorities(new SimpleGrantedAuthority(user.getRole().getKey())) // 권한 추가
			.build();
	}

	@Transactional
	public UserDto update(UserDto userDto, String email) {
		User findUser = userRepository.findByEmail(email);

		if (findUser == null) {
			throw new NoSuchElementException("No user found with the given email: " + email);
		}

		if (!Objects.equals(userDto.getEmail(), email)) {
			throw new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL);
		}

		// 비밀번호 암호화 처리
		String encodedPassword = null;
		if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
			encodedPassword = passwordEncoder.encode(userDto.getPassword());
		}

		// SolvedAC username 유효성 확인 되면 db에 저장하기 위한 기능
		if (userDto.getSolvedacUserName() != null && !userDto.getSolvedacUserName().isEmpty()) {
			boolean isValid = isUsernameValid(userDto.getSolvedacUserName());
			if (!isValid) {
				throw new CustomException(UserErrorCode.INVALID_SOLVEDAC_USERNAME);
			}
		}

		// UserDto에서 업데이트 정보를 반영
		findUser.updateUser(userDto, encodedPassword);

		// Save the updated user entity
		userRepository.save(findUser);

		return ConvertUtils.convertUserToDto(findUser);
	}

	/**
	 * 탈퇴 신청
	 *
	 * @param email 로그인시 email로 로그인
	 * @return user를 repository에 저장
	 * @author jooyoung
	 */
	@Transactional
	public void setDeleted(String email, HttpServletRequest request, HttpServletResponse response) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.setDeleted();
			userRepository.save(user); // 변경 사항 저장

			// 로그아웃 처리
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
			SecurityContextHolder.clearContext();

			// 명시적으로 쿠키 삭제
			Cookie rememberMeCookie = new Cookie("remember-me", null);
			rememberMeCookie.setPath("/");
			rememberMeCookie.setMaxAge(0);
			response.addCookie(rememberMeCookie);

			Cookie sessionCookie = new Cookie("JSESSIONID", null);
			sessionCookie.setPath("/");
			sessionCookie.setMaxAge(0);
			response.addCookie(sessionCookie);
		}
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional
	public void delete(String username) {
		User user = userRepository.findByEmail(username);
		if (user == null || !user.getIsDeleted()) {
			throw new CustomException(UserErrorCode.USER_NOT_EQUAL_EMAIL);
		}

		userRepository.delete(user);
	}

	/**
	 * 계정 삭제 스케줄러
	 *
	 * @return user를 삭제
	 * @author jooyoung
	 * 확인 필요합니다.
	 */
	@Transactional
	//@Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
	@Scheduled(fixedRate = 86400000) // 매일 실행 (24시간 = 86400000 ms)
	public void deleteScheduledUsers() {
		List<User> usersToDelete = userRepository.findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime.now());
		for (User user : usersToDelete) {
			userRepository.delete(user);
		}
	}

	/**
	 * 계정 복구
	 *
	 * @param email 로그인시 email로 로그인
	 * @return user를 repository에 저장
	 * @author jooyoung
	 */
	public void restoreAccount(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.restore();
			userRepository.save(user); // 변경 사항 저장
		}
	}


	/*// 로그인 여부를 확인
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated()
			&& !(authentication instanceof AnonymousAuthenticationToken);
	}*/

	/**
	 * 사용자의 이메일과 사용자 이름으로 비밀번호 재설정 토큰 생성
	 *
	 * @author yuseok
	 * @param email 사용자의 이메일
	 * @param username 사용자의 사용자 이름
	 * @return 비밀번호 재설정에 사용할 토큰, 사용자 정보가 일치하지 않으면 null 반환
	 */
	public String findPassword(String email, String username) {
		// 이메일과 사용자 이름으로 해당 유저 찾기
		Optional<User> userOptional = userRepository.findByEmailAndUsername(email, username);

		// 유저를 찾으면
		if (userOptional.isPresent()) { // isPresent(): Optional 객체가 값을 포함하고 있는지 확인 (객체에 값이 있으면 true, 비어있으면 false 반환)
			// 비밀번호 재설정을 위한 임시 토큰 생성
			String token = UUID.randomUUID().toString();
			User user = userOptional.get();
			user.updatePassword(token); // updatePassword 메서드를 사용해 토큰을 임시 비밀번호로 저장
			userRepository.save(user);
			return token;
		}
		return null;
	}

	/**
	 * 주어진 토큰을 사용하여 사용자의 비밀번호 재설정
	 *
	 * @author yuseok
	 * @param token 비밀번호 재설정에 필요한 토큰
	 * @param newPassword 사용자가 입력한 새 비밀번호
	 * @return 비밀번호 재설정에 성공하면 true, 실패하면 false 반환
	 */
	public boolean resetPassword(String token, String newPassword) {
		// 임시 비밀번호(토큰)으로 유저 찾기
		Optional<User> userOptional = userRepository.findByPassword(token);

		if (userOptional.isPresent()) { // 유저가 존재하면
			User user = userOptional.get();
			user.updatePassword(passwordEncoder.encode(newPassword)); // 새로운 비밀번호로 업데이트
			userRepository.save(user);
			return true;
		}

		return false; // 유저가 없거나 토큰이 잘못된 경우 실패
	}

	/**
	 * 모든 사용자 정보 조회
	 *
	 * @author yuseok
	 * @return 모든 사용자의 정보가 담긴 UserDto 객체들의 리스트
	 */
	public List<UserDto> getAllUsers() {
		// 모든 User 엔티티를 데이터베이스에서 조회 후 리스트에 저장
		List<User> users = userRepository.findAll();

		// UserDto 객체들을 저장할 리스트 초기화
		List<UserDto> userDtos = new ArrayList<>();

		// 각 User 엔티티를 UserDto로 변환 후 리스트에 추가
		for (User user : users) {
			UserDto userDto = ConvertUtils.convertUserToDto(user);
			userDtos.add(userDto);
		}

		// UserDto 리스트 반환
		return userDtos;
	}

	/**
	 * SolvedAC username 유효성 확인
	 *
	 * @author 조아라
	 * @return username의 유효성을 체크하는 boolean
	 * js에서 구현했을때 CORS에러로 인해 서버에서 로직을 처리함.
	 */

	public boolean isUsernameValid(String solvedacUsername) {
		String SOLVEDAC_USERNAME_VALID = "https://solved.ac/api/v3/user/show?handle=";

		try {
			RestTemplate restTemplate = new RestTemplate();
			String apiUrl = SOLVEDAC_USERNAME_VALID + solvedacUsername;

			ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
			// username이 존재하면 true 반환
			return response.getStatusCode().is2xxSuccessful();

		} catch (Exception e) {
			// 존재하지 않는다면 false 반환
			return false;
		}

	}

	/**
	 * home 화면에 출력할 문제 리스트에서 추출
	 *
	 * @author 조아라
	 * @return 추천 문제 String 반환
	 * 리스트에 저장된 문제들 중 랜덤으로 한 문제를 화면에 출력
	 */
    public String getRandomProblemsByUsername(String userEmail) {
		// SolvedACResponseEntity에서 사용자 문제 리스트 가져오기
		Optional<SolvedACResponseEntity> optionalResponseEntity = solvedACResponseRepository.findByUserEmail(userEmail);

		if (optionalResponseEntity.isPresent()) {
			SolvedACResponseEntity responseEntity = optionalResponseEntity.get();
			List<String> recommendedProblems = responseEntity.getResponse();
			String problemToShow = getRandomProblem(recommendedProblems);
			return problemToShow;
		} else {
			return null;
		}

	}

	/**
	 * home 화면에 출력할 문제 리스트에서 추출
	 *
	 * @author 조아라
	 * @return 추천 문제 String 반환
	 * 랜덤으로 한 문제 고르는 메서드
	 */
	private String getRandomProblem(List<String> problems){
		if (problems == null || problems.isEmpty()) {
			return "추천 문제를 가져올 수 없습니다."; // 문제가 없을 때의 처리
		}
		Random random = new Random();
		return problems.get(random.nextInt(problems.size()));
	}

	/**
	 * SolvedAC 문제 추천 리스트 있는지 확인
	 *
	 * @author 조아라
	 * @return 추천받은 문제 리스트가 있는지 확인하는 boolean
	 * 홈 화면 호출을 위한 체크
	 */
	public Boolean checkSolvedACUserNameByUsername(String userEmail) {
		// User 엔티티에서 solvedACUserName을 사용하여 추천 문제 리스트 가져오기
		Optional<SolvedACResponseEntity> optionalResponseEntity = solvedACResponseRepository.findByUserEmail(userEmail);

		// 사용자가 SolvedAC 문제 추천 리스트를 가지고 있지 않은 경우
		if (optionalResponseEntity.isEmpty()) {
			return false; // 또는 null을 반환하여 처리
		}else{
			return true;
		}
	}
}