package com.bookvillage.backend.common;

import javax.servlet.http.HttpServletRequest;

public final class RequestIpResolver {
    private RequestIpResolver() {
    }

    public static String resolve(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String xForwardedFor = trim(request.getHeader("X-Forwarded-For"));
        if (!xForwardedFor.isEmpty()) {
            String[] parts = xForwardedFor.split(",");
            if (parts.length > 0) {
                String first = trim(parts[0]);
                if (!first.isEmpty()) {
                    return first;
                }
            }
        }

        String xRealIp = trim(request.getHeader("X-Real-IP"));
        if (!xRealIp.isEmpty()) {
            return xRealIp;
        }

        return trim(request.getRemoteAddr());
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
