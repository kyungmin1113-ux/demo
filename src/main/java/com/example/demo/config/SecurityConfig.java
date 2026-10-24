package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/hello",
                    "/hello2",
                    "/login",
                    "/signup",
                    "/error",
                    "/detailed_web.html",
                    "/detailed_ai.html",
                    "/detailed_security.html",
                    "/detailed_game.html"
                ).permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                .requestMatchers("/api/hello").permitAll()
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/testdb").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
            )
            .rememberMe(remember -> remember
                .key("gyeongmin-week5-remember-me")
                .tokenValiditySeconds(60 * 60 * 24 * 7)
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, exception) -> {
                if (request.getRequestURI().startsWith(request.getContextPath() + "/api/")) {
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED).commence(request, response, exception);
                } else {
                    new LoginUrlAuthenticationEntryPoint("/login").commence(request, response, exception);
                }
            }));

        return http.build();
    }
}
