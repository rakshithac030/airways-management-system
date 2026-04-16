<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<head>
    <title>User Registration</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/user-card.css">
</head>

<body>

<div class="page">
    <div class="register-card">

        <h2>Register</h2>

        <form action="${pageContext.request.contextPath}/register" method="post">

            <div class="input-box">
                <span class="icon">👤</span>
                <input name="username" placeholder="Username" required />
            </div>

            <div class="input-box">
                <span class="icon">📧</span>
                <input name="email" placeholder="Email" required />
            </div>

            <div class="input-box">
                <span class="icon">🔒</span>
                <input name="password" type="password" placeholder="Password" required />
            </div>
            
			<div class="input-box">
			    <span class="icon">🔒</span>
			    <input name="confirmPassword" type="password" placeholder="Confirm Password" required />
			</div>             
            
             <!-- Error message -->
    		<p id="passwordError" class="error-text"></p>
            
			<div class="input-box">
			    <span class="icon">📞</span>
			    <input name="phoneNumber" placeholder="Phone Number" />
			</div>

            <button class="register-btn">REGISTER</button>
        </form>
        
		<c:if test="${not empty responseMessage}">
		    <p style="color: green; text-align: center;">
		        ${responseMessage}
		    </p>
		</c:if>

        <a class="back-link" href="${pageContext.request.contextPath}/login">
            Already have an account? Login
        </a>

    </div>
</div>

</body>
</html>
