package com.example.algoyweb.controller.temp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChattingViewController {

  @GetMapping("/algoy/chatting-demo")
  public String getChatPage() {
    return "temp/chatting-demo";
  }
}