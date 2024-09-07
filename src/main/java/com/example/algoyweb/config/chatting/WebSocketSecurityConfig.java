package com.example.algoyweb.config.chatting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

  @Bean
  public MessageMatcherDelegatingAuthorizationManager.Builder messageAuthorizationManager(
      MessageMatcherDelegatingAuthorizationManager.Builder messages) {
    messages
        .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
        .simpDestMatchers("/algoy/**").authenticated()
        .simpSubscribeDestMatchers("/topic/**").authenticated()
        .anyMessage().authenticated();

    return messages;
  }
}