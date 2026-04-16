<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Login</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/user-card.css">
</head>

<body>

<div class="page">
    <div class="register-card">

        <h2>Login</h2>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="input-box">
                <span class="icon">👤</span>
                <input type="text" name="username" placeholder="Username" required>
            </div>

            <div class="input-box">
                <span class="icon">🔒</span>
                <input type="password" name="password" placeholder="Password" required>
            </div>

            <button type="submit" class="register-btn">LOGIN</button>
        </form>

        <c:if test="${not empty error}">
            <p style="color:red; text-align:center;">${error}</p>
        </c:if>
        
        <c:if test="${not empty responseMessage}">
    <p style="color:green; text-align:center;">
        ${responseMessage}
    </p>
</c:if>
        
        <a class="back-link" href="${pageContext.request.contextPath}/register">
            New user? Register
        </a>

    </div>
</div>

</body>
</html>
