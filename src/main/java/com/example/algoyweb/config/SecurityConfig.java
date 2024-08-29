package com.example.algoyweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.algoyweb.model.entity.user.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  // 비밀번호 인코더 빈을 생성하는 메서드
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(
            (csrfConfig) -> csrfConfig.disable()
        )
        .headers(
            (headerConfig) -> headerConfig.frameOptions(
                frameOptionsConfig -> frameOptionsConfig.disable()
            )
        )
        .authorizeHttpRequests(
            (requests) -> requests
                .requestMatchers("/algoy").permitAll()
                .requestMatchers("/algoy/sign").permitAll()
                .requestMatchers("/algoy/login").permitAll()
                .requestMatchers("/algoy/login-google").permitAll()
                .requestMatchers("/algoy/check-email-duplicate").permitAll()
                .requestMatchers("/algoy/check-nickname-duplicate").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/posts/new", "/comments/save").hasRole(Role.NORMAL.name())
                .requestMatchers("/", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*", "/posts/**", "/comments/**").permitAll()
                .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/algoy/login")
            .loginProcessingUrl("/algoy/home")
            .defaultSuccessUrl("/algoy/home", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/algoy/login") // Redirect to /algoy/sign after logout
            .invalidateHttpSession(true) // Invalidate session
            .clearAuthentication(true) // Clear authentication
            .deleteCookies("JSESSIONID") // Optionally delete cookies
            .permitAll()
        )
        // OAuth2 로그인 기능에 대한 여러 설정
        .oauth2Login(Customizer.withDefaults()); // 아래 코드와 동일한 결과
        /*
                .oauth2Login(
                        (oauth) ->
                            oauth.userInfoEndpoint(
                                    (endpoint) -> endpoint.userService(customOAuth2UserService)
                            )
                );
        */

    return http.build();
  }
}
