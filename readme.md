# 자바웹프로그래밍 실습 | 2~7주차

**성결대학교 미디어소프트웨어학과 · 20230968 기경민**

Spring Boot로 개인 포트폴리오부터 데이터베이스, 회원 인증·권한 관리, REST API까지 구현한 실습 프로젝트입니다. 수업 내용과 연습문제를 이 README 한 페이지에 통합했습니다.

## 개발 환경 및 실행

Java 26 · Spring Boot 4.1.1 · Maven · Thymeleaf · Bootstrap 5 · Spring Data JPA · Spring Security · MySQL (`spring`) · H2(테스트)

MySQL을 실행하고 아래 SQL로 DB를 준비한 뒤, `src/main/resources/application-secret.properties`에 로컬 DB 계정 정보를 설정합니다. 해당 파일은 Git 추적에서 제외합니다.

```sql
CREATE DATABASE IF NOT EXISTS spring CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
./mvnw spring-boot:run
# 브라우저: http://localhost:8080/
```

관리자는 `/signup`에서 `admin`으로 회원가입한 뒤 [관리자 권한 SQL](관리자_권한_부여.sql)을 실행하고 다시 로그인합니다. 비밀번호는 회원가입 시 BCrypt로 해시하여 저장합니다.

## 주차별 실습 및 연습문제

| 주차 | 학습 주제 | 구현 내용 및 연습문제 |
| --- | --- | --- |
| 2주차 | 개발 환경·기본 테스트 | Spring Boot 실행, Controller와 URL 매핑, `/hello`·`/hello2`에서 Thymeleaf로 인사와 학생 정보 출력 |
| 3주차 | 포트폴리오 프론트 | 프로필 사진과 크기 조정, 자기소개·일대기, 연락 영역 개인화, 웹·AI·보안·게임 상세 페이지와 가격 표시 |
| 4주차 | 데이터베이스 연동 | MySQL `spring` DB 연결, `TestDB` 엔티티·Repository·Service 구성, `/testdb`에서 DB 목록 조회 |
| 5주차 | 로그인·로그아웃·암호화 | 회원가입과 입력 검증, BCrypt, 세션 로그인·로그아웃, remember-me, 로그인 상태별 메뉴 표시 |
| 6주차 | 권한·관리자 페이지 | USER·MANAGER·ADMIN 분리, 내 정보·관리자 회원 목록, 권한 변경·삭제, 403 화면. **연습:** 본인 권한 변경·삭제 차단, MANAGER의 `/testdb` 접근 허용 |
| 7주차 | REST API·JSON | 응답 DTO, Basic/세션 인증, 회원 조회·등록·권한 변경·삭제, 제공 코드의 fetch 화면 연결. **연습①:** 아이디·이름 검색. **연습②:** `@RestControllerAdvice`로 오류 처리 통합 |

## 주요 화면 및 접근 권한

| 구분 | 주소 | 접근 권한 |
| --- | --- | --- |
| 포트폴리오·기본 실습 | `/`, `/hello`, `/hello2` | 공개 |
| 기술별 상세 페이지 | `/detailed_web.html`, `/detailed_ai.html`, `/detailed_security.html`, `/detailed_game.html` | 공개 |
| 회원가입·로그인 | `/signup`, `/login` | 공개 |
| 내 정보 | `/mypage` | 로그인 회원 |
| DB 조회 | `/testdb` | ADMIN·MANAGER |
| 관리자 회원 관리 | `/admin/members`, `/admin/members-api` | ADMIN |

기존 회원 관리 화면의 **API 버전으로 보기**를 누르면 새로고침 없이 JSON으로 회원을 관리하고 HTTP 요청·응답 기록을 확인할 수 있습니다.

## 7주차 REST API

| 메서드 | 주소 | 결과 |
| --- | --- | --- |
| GET | `/api/hello` | 공개, 인사·시간 JSON (200) |
| GET | `/api/me` | 로그인 회원의 내 정보 (200) |
| GET | `/api/admin/members?keyword=student` | ADMIN, 전체 목록 또는 검색 결과 (200) |
| GET | `/api/admin/members/{id}` | ADMIN, 회원 1명 (200), 없으면 404 |
| POST | `/api/admin/members` | ADMIN, 회원 등록 (201 + Location 헤더) |
| PATCH | `/api/admin/members/{id}/role` | ADMIN, 권한 변경 (200) |
| DELETE | `/api/admin/members/{id}` | ADMIN, 회원 삭제 (204, 본문 없음) |

등록 JSON은 `username`, `password`, `passwordConfirm`, `name`을 사용합니다. 신규 회원은 요청에 `role`을 넣어도 USER로 저장하며, 권한 변경은 `{"role":"MANAGER"}` 형식입니다. 응답 DTO에는 `id`, `username`, `name`, `role`만 포함하고 비밀번호·해시는 제외합니다.

**연습① 검색:** 아이디 또는 이름에 검색어가 포함된 회원을 반환합니다. 앞뒤 공백과 영문 대소문자를 처리하며, 빈 검색어는 전체 목록, 검색 결과가 없으면 `[]`를 반환합니다.

**연습② 공통 오류:** 중복 아이디, 필수 값 누락, 비밀번호 불일치, 허용되지 않는 권한, 본인 변경·삭제, 잘못된 JSON·회원 번호를 `400`과 `{"error":"메시지"}`로 처리합니다. 미인증 API는 401, 관리자 권한 부족은 403이며 서버 오류 응답의 스택 추적 노출을 막았습니다.

Postman은 **Authorization → Basic Auth**에 실제 가입한 계정을 입력하고, 등록·수정 요청은 **Body → raw → JSON**으로 전송합니다. 브라우저는 기존 로그인 세션을 사용합니다. 실습 조건에 따라 `/api/**`는 CSRF 검사에서 제외하고 HTML 폼은 검사를 유지합니다. Basic 인증을 외부 서비스에서 사용할 때는 HTTPS가 필요합니다.

## 검증 및 문제 해결

```bash
./mvnw test
curl http://localhost:8080/api/hello
curl -u admin 'http://localhost:8080/api/admin/members?keyword=student'
```

`curl -u admin`은 비밀번호를 별도로 입력받습니다. 앞선 구현 검증에서 자동 테스트 **7개가 통과**했습니다. H2 기반 인증·권한, 세션/Basic 인증, 검색, BCrypt, 비밀번호 비노출, 권한 조작 방지, 등록·변경·삭제, 본인 보호, 공통 오류와 템플릿 렌더링을 검증했습니다. MySQL 연결 및 실제 서버의 `/api/hello` 200, 미인증 `/api/me` 401 응답도 확인했습니다.

학습 중에는 프로필 이미지 경로·크기, 회원가입 HTML 따옴표 누락, DB 연결 설정을 수정했습니다. API 인증 실패 시 로그인 HTML 대신 401을 반환하도록 분리했고, 엔티티를 직접 응답할 때 발생하는 비밀번호 노출은 응답 DTO로 해결했습니다. 화면과 API가 같은 서비스를 사용하므로 권한 검사와 본인 계정 보호 규칙도 함께 적용됩니다.

## 실행 화면

기존 포트폴리오 메인 화면 캡처입니다.

![기경민 포트폴리오 메인 화면](docs/images/index-page.png)
