package com.example.algoyweb.exception.chatting;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.validation.BindException;

public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

  @Override
  public Message<byte[]> handleClientMessageProcessingError(
      Message<byte[]> clientMessage, Throwable ex) {
    Throwable exception = ex;
    if (exception instanceof BindException) {
      return handleBindException((BindException) exception);
    } else if (exception instanceof Exception) {
      return handleException(clientMessage, exception);
    }
    return super.handleClientMessageProcessingError(clientMessage, ex);
  }

  private Message<byte[]> handleBindException(BindException ex) {
    String errorMessage = "유효성 검사 실패: " + ex.getAllErrors().get(0).getDefaultMessage();
    return createErrorMessage(errorMessage);
  }

  private Message<byte[]> handleException(Message<byte[]> clientMessage, Throwable ex) {
    String errorMessage = "예상치 못한 오류가 발생했습니다: " + ex.getMessage();
    return createErrorMessage(errorMessage);
  }

  private Message<byte[]> createErrorMessage(String errorMessage) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
    headerAccessor.setMessage(errorMessage);
    headerAccessor.setLeaveMutable(true);
    return MessageBuilder.createMessage(
        errorMessage.getBytes(), headerAccessor.getMessageHeaders());
  }
}