package com.icps.security;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * 解析 {@code Authorization: Bearer <jwt>} 并写入 SecurityContext。
 *
 * <p>无令牌时直接放行，交由 Security 的 authenticationEntryPoint 返回 401；
 * 令牌存在但校验失败时立即返回 401 JSON。</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = tokenProvider.resolveToken(header);

        if (StringUtils.hasText(token)) {
            try {
                JwtTokenProvider.JwtPrincipal principal = tokenProvider.parseToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + principal.getRole().toUpperCase())));
                authentication.setDetails(principal);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtTokenProvider.JwtValidationException e) {
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"code\":401}");
    }
}
