# 2주차 스프링 부트 개발환경 및 테스트

2주차 연습문제인 URL 매핑과 컨트롤러 추가를 완료한 Spring Boot 프로젝트입니다.

## 개발 환경

- Java 26
- Spring Boot 4.1.1
- Maven
- Thymeleaf

## 실행 방법

```bash
./mvnw spring-boot:run
```

실행 후 브라우저에서 다음 주소로 접속합니다.

- 메인 페이지: `http://localhost:8080/`
- 헬로 페이지: `http://localhost:8080/hello`
- 두번째 헬로 페이지: `http://localhost:8080/hello2`

## 구현 내용

- `DemoController`에 `/hello` URL 매핑 구현
- `DemoController`에 `/hello2` URL 매핑 추가
- `/hello2`에서 5개의 모델 속성 전달
  - `name`
  - `studentId`
  - `subject`
  - `week`
  - `message`
- `hello2.html`에서 5개 속성 변수 출력
- `hello.html`에 `두번째 헬로 페이지` 링크 추가

## 실행 화면

### 메인 페이지

![메인 페이지 실행 화면](docs/images/index-page.png)

### 헬로 페이지

![헬로 페이지 실행 화면](docs/images/hello-page.png)

### 두번째 헬로 페이지

![두번째 헬로 페이지 실행 화면](docs/images/hello2-page.png)

## 주요 파일

- [DemoController.java](src/main/java/com/example/demo/DemoController.java)
- [index.html](src/main/resources/templates/index.html)
- [hello.html](src/main/resources/templates/hello.html)
- [hello2.html](src/main/resources/templates/hello2.html)

## 테스트

```bash
./mvnw test
```

테스트 결과: 성공
