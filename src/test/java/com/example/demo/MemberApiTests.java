package com.example.demo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.example.demo.model.domain.Member;
import com.example.demo.model.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemberApiTests {

    @Autowired MockMvc mvc;
    @Autowired MemberRepository members;
    @Autowired PasswordEncoder encoder;

    private Member admin;
    private Member user;

    @BeforeEach
    void setup() {
        admin = save("api-admin", "관리자", "ADMIN");
        user = save("student1", "기경민", "USER");
        save("manager1", "매니저", "MANAGER");
    }

    @Test
    void authenticationAndAuthorization() throws Exception {
        mvc.perform(get("/api/hello")).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("안녕하세요, REST API!"));
        mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/mypage")).andExpect(status().isFound())
            .andExpect(redirectedUrl("/login"));
        mvc.perform(get("/api/me").header("Authorization", basic("student1", "wrong")))
            .andExpect(status().isUnauthorized());
        for (String username : new String[]{"student1", "manager1"}) {
            mvc.perform(get("/api/admin/members").header("Authorization", basic(username)))
                .andExpect(status().isForbidden());
            mvc.perform(delete("/api/admin/members/" + user.getId()).header("Authorization", basic(username)))
                .andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/me").header("Authorization", basic("student1")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("student1"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void listSearchAndNotFound() throws Exception {
        mvc.perform(get("/api/admin/members").header("Authorization", basic("api-admin")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[*].password").isEmpty());
        for (String keyword : new String[]{" STUDENT ", "경민"}) {
            mvc.perform(get("/api/admin/members").param("keyword", keyword)
                    .header("Authorization", basic("api-admin")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("student1"));
        }
        mvc.perform(get("/api/admin/members").param("keyword", " ")
                .header("Authorization", basic("api-admin")))
            .andExpect(jsonPath("$.length()").value(3));
        mvc.perform(get("/api/admin/members").param("keyword", "absent")
                .header("Authorization", basic("api-admin")))
            .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/admin/members/" + user.getId()).header("Authorization", basic("api-admin")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(get("/api/admin/members/999999").header("Authorization", basic("api-admin")))
            .andExpect(status().isNotFound());
    }

    @Test
    void createUsesBcryptAndIgnoresRoleInjection() throws Exception {
        String json = """
            {"username":"new-user","name":"신규 회원","password":"test1234",
             "passwordConfirm":"test1234","role":"ADMIN"}
            """;
        mvc.perform(post("/api/admin/members").header("Authorization", basic("api-admin"))
                .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.password").doesNotExist());
        Member saved = members.findByUsername("new-user").orElseThrow();
        assertThat(encoder.matches("test1234", saved.getPassword())).isTrue();
        mvc.perform(post("/api/admin/members").header("Authorization", basic("api-admin"))
                .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void updateDeleteAndSelfProtection() throws Exception {
        mvc.perform(patch("/api/admin/members/" + user.getId() + "/role")
                .header("Authorization", basic("api-admin"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"MANAGER\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("MANAGER"));
        for (String role : new String[]{"SUPER", "USER", null}) {
            mvc.perform(patch("/api/admin/members/" + admin.getId() + "/role")
                    .header("Authorization", basic("api-admin"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(role == null ? "{}" : "{\"role\":\"" + role + "\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists());
        }
        mvc.perform(delete("/api/admin/members/" + admin.getId()).header("Authorization", basic("api-admin")))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists());
        mvc.perform(delete("/api/admin/members/" + user.getId()).header("Authorization", basic("api-admin")))
            .andExpect(status().isNoContent()).andExpect(content().string(""));
        assertThat(members.existsById(user.getId())).isFalse();
        assertThat(members.findById(admin.getId()).orElseThrow().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void browserSessionWorksAndHtmlFormsRetainCsrf() throws Exception {
        var loginPage = mvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        var session = (MockHttpSession) loginPage.getRequest().getSession();
        var csrf = (CsrfToken) loginPage.getRequest().getAttribute("_csrf");
        var login = mvc.perform(post("/login").session(session)
                .param("username", "api-admin").param("password", "test1234")
                .param(csrf.getParameterName(), csrf.getToken()))
            .andExpect(status().isFound()).andReturn();
        var authenticatedSession = (MockHttpSession) login.getRequest().getSession();
        mvc.perform(get("/api/me").session(authenticatedSession))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("api-admin"));
        mvc.perform(patch("/api/admin/members/" + user.getId() + "/role").session(authenticatedSession)
                .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"MANAGER\"}"))
            .andExpect(status().isOk());
        mvc.perform(post("/admin/members/" + user.getId() + "/delete").session(authenticatedSession))
            .andExpect(status().isForbidden());
        assertThat(members.existsById(user.getId())).isTrue();
    }

    @Test
    void sharedErrorHandlerAndProvidedPage() throws Exception {
        for (String body : new String[]{"{}", "{\"username\":\"x\"}", "{", "null"}) {
            mvc.perform(post("/api/admin/members").header("Authorization", basic("api-admin"))
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.trace").doesNotExist());
        }
        mvc.perform(get("/api/admin/members/abc").header("Authorization", basic("api-admin")))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists());
        mvc.perform(get("/admin/members-api").header("Authorization", basic("api-admin")))
            .andExpect(status().isOk()).andExpect(view().name("admin/members-api"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("const ME = \"api-admin\"")));
    }

    private Member save(String username, String name, String role) {
        Member member = new Member();
        member.setUsername(username);
        member.setName(name);
        member.setRole(role);
        member.setPassword(encoder.encode("test1234"));
        return members.save(member);
    }

    private String basic(String username) {
        return basic(username, "test1234");
    }

    private String basic(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}
