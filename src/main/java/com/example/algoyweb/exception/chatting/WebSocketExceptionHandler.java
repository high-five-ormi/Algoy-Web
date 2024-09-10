package com.example.algoyweb.exception.chatting;

import com.example.algoyweb.exception.CustomException;
import com.example.algoyweb.model.dto.chatting.ErrorResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.validation.BindException;

/**
 * @author JSW
 *
 * WebSocket 채팅 메시지 처리 과정에서 발생하는 예외를 처리하는 핸들러 클래스입니다.
 * Spring 제공하는 StompSubProtocolErrorHandler 클래스를 상속받아 구현합니다.
 */
public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

  /**
   * 클라이언트가 보낸 메시지를 처리하는 과정에서 예외가 발생했을 때 호출됩니다.
   * 발생한 예외 종류에 따라 적절한 에러 메시지를 클라이언트에게 반환합니다.
   *
   * @param clientMessage 클라이언트가 보낸 메시지
   * @param ex 예외 객체
   * @return 에러 메시지를 포함하는 메시지 객체
   */
  @Override
  public Message<byte[]> handleClientMessageProcessingError(
      Message<byte[]> clientMessage, Throwable ex) {
    Throwable exception = ex;

    // 예외 종류에 따라 분기 처리
    if (exception instanceof BindException) {
      return handleBindException((BindException) exception);
    } else if (exception instanceof CustomException) {
      return handleCustomException((CustomException) exception);
    } else if (exception instanceof Exception) {
      return handleException(clientMessage, exception);
    }

    // 알 수 없는 예외일 경우 기본 처리
    return super.handleClientMessageProcessingError(clientMessage, ex);
  }

  /**
   * 클라이언트 메시지의 유효성 검사 실패(BindException)에 대한 처리 메서드입니다.
   *
   * @param ex 유효성 검사 실패 예외 객체
   * @return 에러 메시지를 포함하는 메시지 객체
   */
  private Message<byte[]> handleBindException(BindException ex) {
    String errorMessage = "유효성 검사 실패: " + ex.getAllErrors().get(0).getDefaultMessage();
    return createErrorMessage(errorMessage);
  }

  /**
   * 커스텀 예외(CustomException)에 대한 처리 메서드입니다.
   *
   * @param ex 커스텀 예외 객체
   * @return 에러 메시지를 포함하는 메시지 객체
   */
  private Message<byte[]> handleCustomException(CustomException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode().getMessage());
    return createErrorMessage(errorResponse.getMessage());
  }

  /**
   * 예상치 못한 예외(Exception)에 대한 처리 메서드입니다.
   *
   * @param clientMessage 클라이언트가 보낸 메시지
   * @param ex 예외 객체
   * @return 에러 메시지를 포함하는 메시지 객체
   */
  private Message<byte[]> handleException(Message<byte[]> clientMessage, Throwable ex) {
    String errorMessage = "예상치 못한 오류가 발생했습니다: " + ex.getMessage();
    return createErrorMessage(errorMessage);
  }

  /**
   * 에러 메시지를 포함하는 메시지 객체를 생성합니다.
   *
   * @param errorMessage 에러 메시지
   * @return 에러 메시지를 포함하는 메시지 객체
   */
  private Message<byte[]> createErrorMessage(String errorMessage) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
    headerAccessor.setMessage(errorMessage);
    headerAccessor.setLeaveMutable(true); // 헤더 수정 가능 설정
    return MessageBuilder.createMessage(
        errorMessage.getBytes(), headerAccessor.getMessageHeaders());
  }
}