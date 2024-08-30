package com.example.algoyweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.algoyweb.service.auth.CustomOAuth2UserService;
import com.example.algoyweb.model.entity.user.Role;

import lombok.RequiredArgsConstructor;

@Configuration // 이 클래스는 설정 클래스
@EnableWebSecurity // 스프링 시큐리티를 활성화
@RequiredArgsConstructor
public class SecurityConfig { // 보안 설정 담당 클래스
  private final CustomOAuth2UserService customOAuth2UserService;

  @Bean
  // 비밀번호 인코더 빈을 생성하는 메서드
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  // HttpSecurity라는 객체를 사용하여 웹 애플리케이션의 보안을 설정
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf((csrfConfig) -> csrfConfig.disable()) // CSRF 보안 기능 비활성화
        .authorizeHttpRequests(
            (requests) -> requests
                .requestMatchers("/").permitAll()
                .requestMatchers("/algoy").permitAll()
                .requestMatchers("/algoy/home", "/algoy/home?continue").permitAll()
                .requestMatchers("/algoy/login").permitAll()
                .requestMatchers("/algoy/sign").permitAll()
                .requestMatchers("/algoy/check-email-duplicate").permitAll()
                .requestMatchers("/algoy/check-nickname-duplicate").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/img/**").permitAll()
                .requestMatchers("/algoy/edituser").authenticated()
                .requestMatchers("/algoy/posts/**", "/algoy/comments/**").hasRole(Role.ADMIN.getKey())
                .requestMatchers("/algoy/posts/**", "/algoy/comments/**").hasRole(Role.NORMAL.getKey())
                // .requestMatchers("/algoy/posts/**", "/algoy/comments/**").hasRole(Role.NORMAL.name())
                .requestMatchers("/algoy/admin/**").hasAuthority(Role.ADMIN.getKey())
                .requestMatchers("/algoy/user/**").hasAuthority(Role.NORMAL.getKey())
                .anyRequest().authenticated())
        .formLogin(
            form -> form
                .loginPage("/algoy/login") // 로그인 페이지를 "/algoy/login"으로 설정
                .loginProcessingUrl("/algoy/login") // 로그인 요청을 처리할 URL 설정
                .successForwardUrl("/algoy/home")
                .defaultSuccessUrl("/algoy/home")
                .failureUrl("/algoy/login?error=true"))
        .logout(
            logout -> logout
                .logoutUrl("/algoy/logout") // 로그아웃을 처리할 URL을 "/algoy/logout"으로 설정: 로그아웃 기능 구현 후 활성화
                .logoutSuccessUrl("/algoy/login") // 로그아웃 후 "/algoy/login" 페이지로 리다이렉트
                .invalidateHttpSession(true) // 로그아웃 시 현재 세션을 무효화하여 세션 데이터 삭제
                .clearAuthentication(true) // 로그아웃 시 현재 인증 정보를 지우기
                .deleteCookies("JSESSIONID", "remember-me") // 로그아웃 시 "JSESSIONID" 쿠키를 삭제하여 세션 쿠키 제거
            )
        .rememberMe(
            rememberMe -> rememberMe
                .key("uniqueAndSecret")
                .tokenValiditySeconds(Integer.MAX_VALUE)
                .rememberMeParameter("remember-me"))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .oauth2Login(
            oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(
                    (request, response, authentication) -> {
                      response.sendRedirect("/algoy/home");
                    }));

    return http.build();
  }
}