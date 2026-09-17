package com.hh.example.study.config;

import com.hh.example.study.service.impl.LogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private LoginLogHandler loginLogHandler;

    @Autowired
    private LogService logService;

    // 密码编码器：使用 BCrypt 加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // REST API 未认证时返回 401 JSON，而不是重定向到登录页
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"请先登录或会话已过期\"}");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    // 1. 放行静态资源
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login.html", "/api/auth/login", "/static/**", "/favicon.ico").permitAll()
                            .anyRequest().authenticated()
                    )

                    // 2. 自定义登录页
                    .formLogin(form -> form
                            .loginPage("/login.html")
                            .loginProcessingUrl("/api/auth/login")
                            .defaultSuccessUrl("/index.html", true)
                            .failureUrl("/login.html?error=true")
                            .successHandler(loginLogHandler)
                            .failureHandler(loginLogHandler)
                            .permitAll()
                    )

                    // 3. 退出登录
                    .logout(logout -> logout
                            .logoutUrl("/api/auth/logout")
                            .logoutSuccessUrl("/logout.html")
                            .permitAll()
                    )

                    // 4. 未认证访问 API 返回 401 JSON，页面访问仍走表单登录重定向
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((request, response, authException) -> {
                                if (request.getRequestURI().startsWith("/api/")) {
                                    restAuthenticationEntryPoint().commence(request, response, authException);
                                } else {
                                    response.sendRedirect("/login.html");
                                }
                            })
                    )

                    // 5. 关闭 CSRF（开发阶段方便测试）
                    .csrf(AbstractHttpConfigurer::disable);

            return http.build();
        }
}
