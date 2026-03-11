package com.bookvillage.backend.config;

import com.bookvillage.backend.entity.User;
import com.bookvillage.backend.repository.UserRepository;
import com.bookvillage.backend.security.UserPrincipal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 취약점: 세션 하이재킹 (Session Hijacking)
 *
 * - SESSION_TOKEN 쿠키 값만으로 인증 처리
 * - IP 주소 바인딩 없음 → 타 단말에서 세션 토큰만 있으면 권한 우회 가능
 * - 로그아웃 후에도 DB의 세션이 active 상태로 유지 → 토큰 재사용 가능
 *
 * 평가 시나리오:
 * 1. 이용자 A가 로그인 → SESSION_TOKEN 쿠키 발급
 * 2. 공격자가 XSS 등으로 document.cookie에서 SESSION_TOKEN 값 탈취
 * 3. 공격자가 별도 IP/단말에서 SESSION_TOKEN 쿠키를 세팅
 * 4. 공격자가 이용자 A의 권한으로 서비스 이용 가능
 */
public class SessionTokenFilter extends OncePerRequestFilter {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public SessionTokenFilter(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 이미 인증된 경우 스킵
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            filterChain.doFilter(request, response);
            return;
        }

        // SESSION_TOKEN 쿠키 또는 X-Session-Token 헤더에서 토큰 추출
        String sessionToken = extractSessionToken(request);

        if (sessionToken != null && !sessionToken.isEmpty()) {
            try {
                // 취약점: IP 검증 없이 토큰만으로 사용자 조회
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                        "SELECT user_id FROM user_sessions WHERE session_key = ? AND active = true",
                        sessionToken);

                if (!rows.isEmpty()) {
                    Long userId = ((Number) rows.get(0).get("user_id")).longValue();
                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null && !"DELETED".equalsIgnoreCase(user.getStatus())) {
                        UserPrincipal principal = new UserPrincipal(user);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        principal, null, principal.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ignored) {
                // 토큰 조회 실패 시 무시하고 다음 필터로
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractSessionToken(HttpServletRequest request) {
        // 1. 쿠키에서 SESSION_TOKEN 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        // 2. 헤더에서 X-Session-Token 찾기 (API 테스트용)
        String headerToken = request.getHeader("X-Session-Token");
        if (headerToken != null && !headerToken.isEmpty()) {
            return headerToken;
        }
        return null;
    }
}
