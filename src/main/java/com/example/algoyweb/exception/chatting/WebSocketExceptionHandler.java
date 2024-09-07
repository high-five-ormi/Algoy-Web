package com.example.algoyweb.exception.chatting;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

  @Override
  public Message<byte[]> handleClientMessageProcessingError(
      Message<byte[]> clientMessage, Throwable ex) {
    Throwable exception = ex;
    if (exception instanceof Exception) {
      return handleException(clientMessage, exception);
    }
    return super.handleClientMessageProcessingError(clientMessage, ex);
  }

  private Message<byte[]> handleException(Message<byte[]> clientMessage, Throwable ex) {
    String errorMessage = "An unexpected error occurred: " + ex.getMessage();

    StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
    headerAccessor.setMessage(errorMessage);
    headerAccessor.setLeaveMutable(true);

    return MessageBuilder.createMessage(
        errorMessage.getBytes(), headerAccessor.getMessageHeaders());
  }
}