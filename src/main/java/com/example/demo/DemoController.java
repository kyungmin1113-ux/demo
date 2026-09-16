package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("data", "반갑습니다.");
        return "hello";
    }

    @GetMapping("/hello2")
    public String hello2(Model model) {
        model.addAttribute("name", "기경민");
        model.addAttribute("studentId", "20230968");
        model.addAttribute("subject", "윈도우 프로그래밍");
        model.addAttribute("week", "2주차");
        model.addAttribute("message", "스프링 부트 URL 매핑과 컨트롤러 연습문제 완료");
        return "hello2";
    }
}
