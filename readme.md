# 2주차/3주차 실습 정리

Spring Boot 개발 환경을 구성하고 URL 매핑, Thymeleaf 화면 출력, 포트폴리오 프론트 페이지 수정까지 진행한 실습 프로젝트입니다.  
2주차는 Spring Boot 실행과 컨트롤러 매핑을 중심으로 구현했고, 3주차는 개인 포트폴리오 화면과 기술별 상세 페이지를 작성했습니다.

## 개발 환경

- Java 26
- Spring Boot 4.1.1
- Maven
- Thymeleaf
- Bootstrap 5
- Bootstrap Icons

## 실행 방법

```bash
./mvnw spring-boot:run
```

실행 후 브라우저에서 아래 주소로 접속합니다.

| 구분 | 주소 |
| --- | --- |
| 메인 포트폴리오 | `http://localhost:8080/` |
| 헬로 페이지 | `http://localhost:8080/hello` |
| 두번째 헬로 페이지 | `http://localhost:8080/hello2` |
| 웹 개발 상세 | `http://localhost:8080/detailed_web.html` |
| AI 상세 | `http://localhost:8080/detailed_ai.html` |
| 보안 상세 | `http://localhost:8080/detailed_security.html` |
| 게임 개발 상세 | `http://localhost:8080/detailed_game.html` |

## 구현 내용

| 주차 | 구현 내용 |
| --- | --- |
| 2주차 | Spring Boot 프로젝트 실행, `DemoController` 작성, `/hello`, `/hello2` URL 매핑, Model 데이터 전달 |
| 3주차 | 포트폴리오 템플릿 수정, 자기소개/일대기 작성, 기술 영역 구성, 기술별 상세 페이지 4개 추가 |

## 핵심 코드

### 1. URL 매핑 컨트롤러

```java
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
```

### 2. Thymeleaf 데이터 출력

```html
<h1>두번째 헬로 페이지</h1>
<ul>
    <li>이름: <span th:text="${name}"></span></li>
    <li>학번: <span th:text="${studentId}"></span></li>
    <li>과목: <span th:text="${subject}"></span></li>
    <li>주차: <span th:text="${week}"></span></li>
    <li>메시지: <span th:text="${message}"></span></li>
</ul>
```

### 3. 포트폴리오 기술 카드 링크

```html
<section class="services section-padding" id="section_3">
    <h2 class="text-white ms-4 mb-0">기술</h2>

    <h3 class="mb-0">웹 개발</h3>
    <p>HTML, CSS, JavaScript와 Spring Boot를 활용해 사용자에게 필요한 웹 서비스를 만들고 싶습니다.</p>
    <a th:href="@{/detailed_web.html}" href="/detailed_web.html" class="custom-btn custom-border-btn btn mt-3">자세히 보기</a>

    <h3 class="mb-0">AI</h3>
    <p>데이터를 이해하고 활용하는 인공지능 기술에 관심이 있으며, 웹 서비스와 AI 기능을 연결해보고 싶습니다.</p>
    <a th:href="@{/detailed_ai.html}" href="/detailed_ai.html" class="custom-btn custom-border-btn btn mt-3">자세히 보기</a>

    <h3 class="mb-0">보안</h3>
    <p>HTTPS, 인증, 권한 관리처럼 사용자의 정보를 안전하게 지키는 웹 보안 분야를 더 깊게 공부하고 싶습니다.</p>
    <a th:href="@{/detailed_security.html}" href="/detailed_security.html" class="custom-btn custom-border-btn btn mt-3">자세히 보기</a>

    <h3 class="mb-0">게임 개발</h3>
    <p>기획, 그래픽, 프로그래밍이 함께 움직이는 게임 개발 분야에 관심이 있으며 작은 프로젝트부터 구현해보고 싶습니다.</p>
    <a th:href="@{/detailed_game.html}" href="/detailed_game.html" class="custom-btn custom-border-btn btn mt-3">자세히 보기</a>
</section>
```

## 포트폴리오 구현 내용

- TemplateMo First Portfolio 템플릿을 기반으로 메인 페이지 구성
- 상단 소개 문구를 `성결대학교 미소과 23학번 기경민입니다.`로 수정
- `profile.jpg`를 적용해 프로필 이미지 표시
- `갱's 일대기` 영역을 개인 소개 내용으로 작성
- 성결대학교 미디어소프트웨어학과 23학번 기경민 소개 문장 작성
- 소개 영역 오른쪽 이미지 크기와 비율 조정
- 프로젝트 영역을 관심 분야에 맞게 수정
- 푸터와 연락 영역의 문구를 개인 포트폴리오에 맞게 수정
- 기존 `Services` 영역을 `기술` 영역으로 변경
- 관심 기술 4가지를 카드 형태로 구성
  - 웹 개발
  - AI
  - 보안
  - 게임 개발
- 각 기술 카드에 Bootstrap Icons 적용
- 각 기술 카드에서 상세 페이지로 이동하도록 링크 연결
- 기술 설명을 개인 관심 분야에 맞게 작성
- `resources/public` 폴더에 기술별 상세 페이지 추가
- `detailed_web.html`의 디자인을 기준으로 나머지 상세 페이지 스타일 통일
- 각 상세 페이지에 분야명, 설명, 가격, 기술 스택, 숙련도, 진행 방식 작성
- 메인으로 돌아가기 버튼 추가

## 상세 페이지 구성

| 분야 | 파일 | 가격 | 주요 내용 |
| --- | --- | --- | --- |
| 웹 개발 | `detailed_web.html` | `$2,400` | 프론트엔드, 백엔드, API, 반응형 웹 |
| AI | `detailed_ai.html` | `$1,200` | AI 개념, 데이터 분석, 서비스 적용 |
| 보안 | `detailed_security.html` | `$3,600` | 인증/권한, HTTPS, 웹 취약점 |
| 게임 개발 | `detailed_game.html` | `$1,450` | 게임 기획, 게임 로직, 사용자 경험 |

## 실행 화면

### 메인 페이지

![메인 페이지 실행 화면](docs/images/index-page.png)

### 상세 페이지 예시

![웹 개발 상세 페이지](docs/images/detailed-web-page.png)

## 주요 파일

- [DemoController.java](src/main/java/com/example/demo/DemoController.java)
- [hello.html](src/main/resources/templates/hello.html)
- [hello2.html](src/main/resources/templates/hello2.html)
- [index.html](src/main/resources/templates/index.html)
- [detailed_web.html](src/main/resources/public/detailed_web.html)
- [detailed_ai.html](src/main/resources/public/detailed_ai.html)
- [detailed_security.html](src/main/resources/public/detailed_security.html)
- [detailed_game.html](src/main/resources/public/detailed_game.html)

## 테스트

```bash
./mvnw test
```

테스트 결과: 성공
