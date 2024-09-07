package com.example.algoyweb.config.chatting;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author JSW
 *
 * WebSocket 설정을 위한 구성 클래스입니다.
 * WebSocketMessageBrokerConfigurer 인터페이스를 구현하여 WebSocket 메시지 브로커를 설정합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * 메시지 브로커를 구성합니다.
   *
   * @param config MessageBrokerRegistry 객체로, 메시지 브로커 설정을 위한 레지스트리입니다.
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // "/topic" 접두사를 가진 메시지 브로커를 활성화합니다.
    config.enableSimpleBroker("/topic");
    // 애플리케이션 목적지 접두사를 "/app"으로 설정합니다.
    config.setApplicationDestinationPrefixes("/app");
  }

  /**
   * STOMP 엔드포인트를 등록합니다.
   *
   * @param registry StompEndpointRegistry 객체로, STOMP 엔드포인트 설정을 위한 레지스트리입니다.
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // "/chat-websocket" 엔드포인트를 추가하고, 모든 출처에서의 접근을 허용합니다.
    registry.addEndpoint("/chat-websocket")
        .setAllowedOriginPatterns("*")
        .withSockJS(); // SockJS를 사용하여 WebSocket을 지원하지 않는 브라우저에서도 동작하도록 합니다.
  }
}