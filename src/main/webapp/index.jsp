<%--  
  Created by IntelliJ IDEA.  
  User: USER  
  Date: 24/05/2025  
  Time: 12.15  
  To change this template use File | Settings | File Templates.  
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  response.sendRedirect(request.getContextPath() + "/home");
%>
<html>
<head>
    <title>Hang Tight! Redirecting...</title>
</head>
<body style="text-align:center; font-family:sans-serif; margin-top:50px;">
    <h2>Hang tight! We're taking you home...</h2>
    <p>If you are not redirected automatically, <a href="<%= request.getContextPath() + "/home" %>">click here</a>.</p>
</body>
</html>
