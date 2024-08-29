package com.example.algoyweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.algoyweb.config.auth.CustomOAuth2UserService;
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
        /*.csrf(
            (csrfConfig) -> csrfConfig.disable() // CSRF 보안 기능 비활성화
        )*/
        /*.headers(
            (headerConfig) -> headerConfig.frameOptions(
                frameOptionsConfig -> frameOptionsConfig.disable()
            )
        )*/
        .authorizeHttpRequests(
            (requests) -> requests
                .requestMatchers("/").permitAll()
                .requestMatchers("/algoy").permitAll()
                .requestMatchers("/algoy/home").permitAll() // 가장 메인이 될 화면(index.html), 임의로 넣어서 바뀔 수 있음
                .requestMatchers("/algoy/sign").permitAll()
                .requestMatchers("/algoy/login").permitAll()
                .requestMatchers("/algoy/login-google").permitAll()
                .requestMatchers("/algoy/check-email-duplicate").permitAll()
                .requestMatchers("/algoy/check-nickname-duplicate").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/algoy/posts/**", "/algoy/comments/**").hasRole(Role.ADMIN.getKey())
                .requestMatchers("/algoy/posts/**", "/algoy/comments/**").hasRole(Role.NORMAL.getKey())
                // .requestMatchers("/algoy/posts/**", "/algoy/comments/**").hasRole(Role.NORMAL.name())
                .requestMatchers("/algoy/admin/**").hasAuthority(Role.ADMIN.getKey())
                .requestMatchers("/algoy/user/**").hasAuthority(Role.NORMAL.getKey())
                .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/algoy/login") // 로그인 페이지를 "/algoy/login"으로 설정
            .loginProcessingUrl("/algoy/login") // 로그인 요청을 처리할 URL 설정
            .defaultSuccessUrl("/algoy/home", true) // 로그인 성공 후 "/algoy/home"으로 리다이렉트
            .permitAll()
        )
        .logout(logout -> logout
            // .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            // .logoutUrl("/algoy/logout") // 로그아웃을 처리할 URL을 "/algoy/logout"으로 설정: 로그아웃 기능 구현 후 활성화
            .logoutSuccessUrl("/algoy/login") // 로그아웃 후 "/algoy/login" 페이지로 리다이렉트
            .invalidateHttpSession(true) // 로그아웃 시 현재 세션을 무효화하여 세션 데이터 삭제
            .clearAuthentication(true) // 로그아웃 시 현재 인증 정보를 지우기
            .deleteCookies("JSESSIONID") // 로그아웃 시 "JSESSIONID" 쿠키를 삭제하여 세션 쿠키 제거
            .permitAll() // 이 로그아웃 URL에 대한 접근을 모든 사용자에게 허용
        )
        // OAuth2 소셜 로그인을 기본 설정으로 활성화 (구글 로그인 가능)
        .oauth2Login(Customizer.withDefaults()) // 아래 코드와 동일한 결과
        // OAuth2 로그인 기능에 대한 여러 설정
        /*.oauth2Login(
            (oauth) ->
                oauth.userInfoEndpoint(
                    (endpoint) -> endpoint.userService(customOAuth2UserService)
                )
        );*/
      .oauth2Login(oauth2 -> oauth2
          .userInfoEndpoint(userInfo -> userInfo
              .userService(customOAuth2UserService))
          .successHandler((request, response, authentication) -> {
              response.sendRedirect("/algoy/home");
          })
      );
            .authorizeHttpRequests(
                    (requests) -> requests
                            .requestMatchers("/algoy").permitAll()
                            .requestMatchers("/algoy/sign").permitAll()
                            .requestMatchers("/algoy/login").permitAll()
                            .requestMatchers("/algoy/home").permitAll()
                            .requestMatchers("/algoy/check-email-duplicate").permitAll()
                            .requestMatchers("/algoy/check-nickname-duplicate").permitAll()
                            .requestMatchers("/css/**").permitAll()
                            .requestMatchers("/js/**").permitAll()
                            .requestMatchers("/img/**").permitAll()
                            .anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())
            .formLogin((form) -> form
                    .loginPage("/algoy/login")
                    .successForwardUrl("/algoy/home")
                    .defaultSuccessUrl("/algoy/home")
                    .failureUrl("/algoy/login?error=true"))
            .logout((logout) -> logout
                    .logoutUrl("/algoy/logout")
                    .logoutSuccessUrl("/algoy/login")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me"))
            .rememberMe(rememberMe -> rememberMe
                    .key("uniqueAndSecret")
                    .tokenValiditySeconds(Integer.MAX_VALUE))
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );

    return http.build();
  }
}
