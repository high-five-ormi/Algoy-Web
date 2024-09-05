package com.example.algoyweb.controller.temp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author JSW
 *
 * ChatBotViewController는 챗봇 페이지를 위한 뷰를 처리하는 Spring MVC 컨트롤러입니다.
 * 이 컨트롤러는 "/algoy/bot/temp" 경로로 들어오는 GET 요청을 처리하며,
 * ai-backend.url 설정값을 모델에 추가하여 뷰에 전달합니다.
 */
@Controller
public class ChatBotViewController {

  /**
   * ai-backend.url 설정값을 저장하는 변수입니다.
   * 이 변수는 application.properties 또는 application.yml 파일에서 정의된 값을 주입받습니다.
   */
  @Value("${ai-backend.url}")
  private String backendUrl;

  /**
   * "/algoy/bot/temp" 경로에 대한 GET 요청을 처리하는 메서드입니다.
   * 이 메서드는 backendUrl 값을 모델에 추가하고, "temp/chatbot" 뷰를
   * 반환하여 해당 뷰에서 이 값을 사용할 수 있도록 합니다.
   *
   * @param model Spring Framework의 Model 객체로, 뷰에 데이터를 전달하기 위해 사용됩니다.
   * @return "temp/chatbot" 뷰의 경로를 반환합니다.
   */
  @GetMapping("/algoy/chatbot-demo")
  public String chatPage(Model model) {
    model.addAttribute("backendUrl", backendUrl);
    return "temp/chatbot-demo";
  }
}