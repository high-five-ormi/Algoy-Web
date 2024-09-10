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
   * 메시지 브로커를 설정합니다.
   *
   * @param config 메시지 브로커 설정을 위한 객체
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 단순한 브로커를 활성화하고, "/topic" 접두사를 사용하는 목적지를 설정합니다.
    config.enableSimpleBroker("/topic");
    // 애플리케이션의 목적지 접두사를 설정합니다.
    config.setApplicationDestinationPrefixes("/algoy");
  }

  /**
   * STOMP 엔드포인트를 등록합니다.
   *
   * @param registry 엔드포인트 등록을 위한 객체
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/algoy/chat-websocket")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }
}