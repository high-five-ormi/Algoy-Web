package com.example.algoyweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
