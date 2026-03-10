package com.bookvillage.backend.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ServerInfoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Server", "BookVillage/2.1.0 (Apache Tomcat/9.0.83)");
        httpResponse.setHeader("X-Powered-By", "Spring Boot 2.7.18 / Java " + System.getProperty("java.version"));
        httpResponse.setHeader("X-Application-Context", "bookvillage-mock:8080");
        httpResponse.setHeader("X-Debug-DB", "MySQL 8.0 @ localhost:3407");
        chain.doFilter(request, response);
    }
}
