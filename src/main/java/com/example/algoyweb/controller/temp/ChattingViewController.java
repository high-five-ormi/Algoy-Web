package com.example.algoyweb.controller.temp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author JSW
 *
 * 채팅 데모 페이지를 처리하는 컨트롤러입니다.
 */
@Controller
public class ChattingViewController {

  /**
   * 채팅 데모 페이지를 반환합니다.
   *
   * @return 채팅 데모 페이지의 이름 (temp/chatting-demo)
   */
  @GetMapping("/algoy/chatting-demo")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public String getChatPage() {
    return "temp/chatting-demo";
  }
}