package com.example.algoyweb.model.entity.auth;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

/**
 * OAuth2 사용자 정보를 담고 있는 클래스
 *
 * @author yuseok
 */
@Getter
public class CustomOAuth2User implements OAuth2User, UserDetails {
	private UserDetails userDetails; // UserDetails 객체를 저장할 변수
	private Map<String, Object> attributes; // OAuth2 사용자 속성을 저장할 맵
	private String nameAttributeKey; // 사용자 이름 속성의 키

	public CustomOAuth2User(UserDetails userDetails, Map<String, Object> attributes, String nameAttributeKey) {
		this.userDetails = userDetails;
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userDetails.getAuthorities();
	}

	@Override
	public String getName() {
		return Objects.requireNonNull(getAttribute(nameAttributeKey)).toString();
	}

	@Override
	public String getPassword() {
		return userDetails.getPassword();
	}

	@Override
	public String getUsername() {
		return userDetails.getUsername();
	}
}