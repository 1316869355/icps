package com.icps.config;

import com.icps.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Spring Security配置类
 *
 * <p>对外暴露的接口全部走 JWT 认证：登录成功后签发 HS256 令牌，
 * 后续请求通过 {@code Authorization: Bearer <token>} 校验。</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** 无需认证即可访问的路径 */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/auth/logout",
            "/auth/health",
            "/auth/refresh-token",
            "/actuator/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 未认证（缺少或无效令牌）时的响应
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"success\":false,\"message\":\"未认证或令牌无效\",\"code\":401}");
        };
    }

    /**
     * 已认证但权限不足时的响应
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"success\":false,\"message\":\"权限不足\",\"code\":403}");
        };
    }

    /**
     * 安全配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // 禁用CSRF保护，因为我们使用的是token认证
            .csrf().disable()

            // 配置CORS
            .cors().configurationSource(corsConfigurationSource())
            .and()

            // 配置请求授权
            .authorizeHttpRequests(authorize -> authorize
                // 允许所有用户访问的接口
                .antMatchers(PUBLIC_ENDPOINTS).permitAll()
                // 其余接口必须携带有效 JWT
                .anyRequest().authenticated()
            )

            // 未认证 / 权限不足时返回 JSON
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )

            // JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 配置会话管理
            .sessionManagement(session -> session
                // 禁用会话，因为我们使用的是token认证
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .build();
    }
}
