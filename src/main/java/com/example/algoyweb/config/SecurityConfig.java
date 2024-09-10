package com.example.algoyweb.config;

import com.example.algoyweb.util.user.UserAuthenticationSuccessHandler;
import com.example.algoyweb.service.allen.AllenService;
import com.example.algoyweb.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.algoyweb.service.auth.CustomOAuth2UserService;

@Configuration // 이 클래스는 설정 클래스
@EnableWebSecurity // 스프링 시큐리티를 활성화
/*메서드 수준의 보안을 활성화하고, @Secured 및 @PreAuthorize/@PostAuthorize 어노테이션을 사용 가능하게 함
securedEnabled => Secured 어노테이션 사용 여부, prePostEnabled => PreAuthorize 어노테이션 사용 여부 (default 가 true 여서 따로 정의하지 않음)*/
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig { // 보안 설정 담당 클래스
	private final CustomOAuth2UserService customOAuth2UserService;

	@Lazy
	private final UserService userService;

	@Lazy
	private final AllenService allenService;

	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, @Lazy UserService userService, @Lazy AllenService allenService) {
		this.customOAuth2UserService = customOAuth2UserService;
		this.userService = userService;
		this.allenService = allenService;
	}

	@Bean
	// 비밀번호 인코더 빈을 생성하는 메서드
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	// HttpSecurity라는 객체를 사용하여 웹 애플리케이션의 보안을 설정
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf((csrf) -> csrf
						.ignoringRequestMatchers("/algoy/**")
						.disable()
				) // CSRF 보안 기능 비활성화
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/").permitAll()
						.requestMatchers("/algoy").permitAll()
						.requestMatchers("/algoy/home/**").permitAll()
						.requestMatchers("/algoy/login").permitAll()
						.requestMatchers("/algoy/sign").permitAll()
						.requestMatchers("/algoy/user/nickname").permitAll()
						.requestMatchers("/algoy/validate-username").permitAll() // solvedAC username 유효성 검사를 위해 추가 by 조아라
						.requestMatchers("/algoy/find-password").permitAll()
						.requestMatchers("/algoy/set-password").permitAll()
						.requestMatchers("/algoy/check-email-duplicate").permitAll()
						.requestMatchers("/algoy/check-nickname-duplicate").permitAll()
						.requestMatchers("/algoy/allen/**").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/js/**").permitAll()
						.requestMatchers("/img/**").permitAll()
						.requestMatchers("/algoy/chat-websocket/**").authenticated() // WebSocket 엔드포인트 보호
						.anyRequest().authenticated()) // 이외의(위에서 정의되지 않은) 모든 경로(요청)는 인증된 사용자만 허용
				.formLogin(form -> form
						.loginPage("/algoy/login")
						.loginProcessingUrl("/algoy/login") // 로그인 요청을 처리할 URL 설정
						.successForwardUrl("/algoy/home")
						.defaultSuccessUrl("/algoy/home")
						//.successHandler(customAuthenticationSuccessHandler)
						.successHandler(userAuthenticationSuccessHandler())
						.failureUrl("/algoy/login?error=true"))
				.logout(logout -> logout
						.logoutUrl("/algoy/logout")
						.logoutSuccessUrl("/algoy/home")
						.invalidateHttpSession(true) // 로그아웃 시 현재 세션을 무효화하여 세션 데이터 삭제
						.clearAuthentication(true) // 로그아웃 시 현재 인증 정보를 지우기
						.deleteCookies("JSESSIONID", "remember-me") // 로그아웃 시 "JSESSIONID" 쿠키를 삭제하여 세션 쿠키 제거
				)
				.rememberMe(rememberMe -> rememberMe
						.key("uniqueAndSecret")
						.tokenValiditySeconds(Integer.MAX_VALUE)
						.rememberMeParameter("remember-me"))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
						.successHandler((request, response, authentication) -> {
							response.sendRedirect("/algoy/home");
						}));


		return http.build();
	}

	@Bean
	public UserAuthenticationSuccessHandler userAuthenticationSuccessHandler() {
		return new UserAuthenticationSuccessHandler(userService, allenService);
	}

}