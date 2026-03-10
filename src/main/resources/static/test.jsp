<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Test Page</title></head>
<body>
<h1>Server Test Page</h1>
<p>Server: Apache Tomcat/9.0.65</p>
<p>Java: <%= System.getProperty("java.version") %></p>
<p>OS: <%= System.getProperty("os.name") %></p>
<p>Status: OK</p>
<!-- DB: jdbc:mysql://localhost:3407/bookvillage_mock -->
<!-- credentials: root / 1234 -->
</body>
</html>
