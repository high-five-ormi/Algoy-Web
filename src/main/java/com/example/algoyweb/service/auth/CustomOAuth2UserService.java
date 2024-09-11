package com.example.algoyweb.service.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.algoyweb.model.dto.auth.OAuthAttributes;
import com.example.algoyweb.model.entity.auth.CustomOAuth2User;
import com.example.algoyweb.model.entity.auth.SessionUser;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.user.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuseok
 *
 * OAuth2 사용자 인증을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;
	private final HttpSession httpSession; // HTTP 세션 관리

	/**
	 * OAuth2 사용자 정보를 로드하는 메서드
	 *
	 * @author yuseok
	 * @param userRequest OAuth2 사용자 요청
	 * @return 로드된 OAuth2 사용자 정보
	 * @throws OAuth2AuthenticationException 인증 예외 발생 시
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// 기본 OAuth2 사용자 서비스
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

		OAuth2User oAuth2User = delegate.loadUser(userRequest); // 사용자 정보 로드

		// 로그인 진행 중인 서비스를 구분: 네이버로 로그인 진행 중인지, 구글로 로그인 진행 중인지... 등을 구분
		String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 등록 ID 가져오기;

		/*사용자 이름 속성 이름 가져오기
		OAuth2 로그인 진행 시 키가 되는 필드 값(Primary Key와 같은 의미)
		구글의 경우 기본적으로 코드를 지원하지만 네이버, 카카오 등은 기본적으로 지원하지 않는다.*/
		String userNameAttributeName = userRequest
			.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		// OAuth 속성 생성: OAuth2UserService를 통해 가져온 OAuth2User의 attribute 등을 담을 클래스
		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
			oAuth2User.getAttributes());

		User user = saveOrUpdate(attributes); // 사용자 정보 저장 또는 업데이트
		httpSession.setAttribute("user", new SessionUser(user)); // 세션에 사용자 정보 저장

		// UserDetails 객체 생성
		UserDetails userDetails = org.springframework.security.core.userdetails.User
			.withUsername(user.getUsername()) // 사용자 이름 설정
			.username(user.getEmail()) // 유저이름에 이메일로 (UserDetails로 이메일을 읽어오는 용도)
			.password(user.getPassword()) // 비밀번호 설정
			.authorities(new SimpleGrantedAuthority(user.getRole().getKey())) // 사용자 역할에 대한 권한 설정
			.build();

		// OAuth2User를 구현하는 사용자 정의 클래스 반환 (UserDetails 객체, OAuth2 속성 맵, 이름 속성 키)
		return new CustomOAuth2User(userDetails, attributes.getAttributes(), attributes.getNameAttributeKey());
	}

	private User saveOrUpdate(OAuthAttributes attributes) {
		User user = userRepository.findByEmail(attributes.getEmail()); // 이메일로 사용자 찾기

		if (user == null) { // 사용자가 존재하지 않는 경우, OAuth2 속성을 기반으로 새로운 사용자 엔티티 생성
			user = attributes.toEntity(); // OAuthAttributes에서 사용자 엔티티로 변환
		} else { // 사용자가 이미 존재하는 경우, OAuth2 속성으로 기존 사용자 정보 업데이트
			user.update(
				attributes.getUsername(), // 사용자 이름
				user.getNickname(), // 사용자 닉네임
				attributes.getEmail(), // 사용자 이메일
				attributes.getPassword(), // 사용자 비밀번호
				user.getRole(), // 기존 역할 유지
				user.getIsDeleted() // 기존 삭제 상태 유지
			);
		}

		user = userRepository.save(user);

		// 저장된 사용자 정보 로깅
		log.info("Saved user: {}", user);

		return user; // 저장된 사용자 반환
	}
}