package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.ApiException;
import com.bookvillage.backend.common.RequestIpResolver;
import com.bookvillage.backend.common.SuccessResponse;
import com.bookvillage.backend.dto.ChangePasswordRequest;
import com.bookvillage.backend.request.LoginRequest;
import com.bookvillage.backend.response.LoginResponse;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController("adminAuthController")
@RequestMapping("/admin/api/auth")
public class AdminAuthController {
    private final JdbcTemplate jdbcTemplate;
    private final InMemoryDataStore store;

    public AdminAuthController(JdbcTemplate jdbcTemplate, InMemoryDataStore store) {
        this.jdbcTemplate = jdbcTemplate;
        this.store = store;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = RequestIpResolver.resolve(httpRequest);
        String username = request == null ? "" : trim(request.username);
        String password = request == null ? "" : trim(request.password);

        try {
            if (username.isEmpty() || password.isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 입력해주세요.");
            }

            // Legacy fallback for old UI hint.
            if ("admin".equalsIgnoreCase(username) && "admin1234".equals(password)) {
                LoginResponse response = buildLoginResponse(0L, "관리자", "admin@bookstore.kr");
                store.recordAccessLog(0L, "/api/auth/login", "LOGIN", clientIp);
                return response;
            }

            Map<String, Object> adminUser = findAdminUser(username);
            if (adminUser == null) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
            }

            String storedPassword = asString(adminUser.get("password"));
            if (!matchesPassword(password, storedPassword)) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
            }

            long userId = asLong(adminUser.get("id"), 0L);
            String name = asString(adminUser.get("name"));
            String email = asString(adminUser.get("email"));
            LoginResponse response = buildLoginResponse(userId, name, email);
            store.recordAccessLog(userId, "/api/auth/login", "LOGIN", clientIp);
            return response;
        } catch (ApiException ex) {
            store.recordAccessLog(null, "/api/auth/login", "LOGIN_FAIL", clientIp);
            throw ex;
        }
    }

    @PostMapping("/change-password")
    public SuccessResponse changePassword(@RequestBody ChangePasswordRequest request) {
        String currentPassword = request == null ? "" : trim(request.getCurrentPassword());
        String newPassword = request == null ? "" : trim(request.getNewPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "현재 비밀번호와 새 비밀번호를 입력해주세요.");
        }

        if (newPassword.length() < 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "새 비밀번호는 8자 이상이어야 합니다.");
        }

        List<Map<String, Object>> admins = jdbcTemplate.queryForList(
                "SELECT id, password FROM users WHERE role = 'ADMIN' ORDER BY id ASC LIMIT 1"
        );

        if (admins.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "관리자 계정을 찾을 수 없습니다.");
        }

        Map<String, Object> admin = admins.get(0);
        long adminId = asLong(admin.get("id"), 0L);
        String storedPassword = asString(admin.get("password"));

        if (!matchesPassword(currentPassword, storedPassword)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE id = ?",
                sha1(newPassword),
                adminId
        );

        return new SuccessResponse(true);
    }

    private Map<String, Object> findAdminUser(String username) {
        String lower = username.toLowerCase(Locale.ROOT);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, email, password, name, role FROM users "
                        + "WHERE role = 'ADMIN' AND (LOWER(email) = ? OR LOWER(email) LIKE ? OR LOWER(name) = ?) "
                        + "ORDER BY id ASC LIMIT 1",
                lower,
                lower + "@%",
                lower
        );

        if (rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    private LoginResponse buildLoginResponse(long userId, String name, String email) {
        String token = "mock-jwt-token-admin-" + userId + "-" + System.currentTimeMillis();
        return new LoginResponse(token, new LoginResponse.User(name, email));
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        if (storedPassword.equals(rawPassword)) {
            return true;
        }
        String sha1 = sha1(rawPassword);
        return storedPassword.equalsIgnoreCase(sha1);
    }

    private String sha1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 암호화에 실패했습니다.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private long asLong(Object value, long fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
