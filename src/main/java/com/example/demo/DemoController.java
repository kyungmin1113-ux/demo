package com.example.demo;

import java.util.List;

import com.example.demo.model.domain.TestDB;
import com.example.demo.model.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    private final TestService testService;

    public DemoController(TestService testService) {
        this.testService = testService;
    }

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

    @GetMapping("/testdb")
    public String testdb(Model model) {
        testService.saveSampleDataIfEmpty();

        TestDB test = testService.findByName("홍길동");
        List<TestDB> users = testService.findAll();

        model.addAttribute("test", test);
        model.addAttribute("users", users);
        model.addAttribute("count", users.size());
        return "testdb";
    }
}
