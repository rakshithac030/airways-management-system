<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.loggedInUser}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/app.css"/>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/user.css"/>
</head>

<body class="bg-light">

<jsp:include page="navbar.jsp"/>

<div class="user-page">

    <h2 class="mb-3">User List</h2>

    <table class="user-table">
        <thead>
        <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <c:if test="${sessionScope.role == 'ADMIN'}">
                <th>Action</th>
            </c:if>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="u" items="${Users}">
            <tr>
                <td>${u.username}</td>
                <td>${u.email}</td>
                <td>${u.role}</td>

                <c:if test="${sessionScope.role == 'ADMIN'}">
                    <td>
                        <form action="${pageContext.request.contextPath}/user/delete/${u.userId}" 
			              method="post" 
			              onsubmit="return confirm('Are you sure you want to delete this user?');"
			              style="display: inline;">
			            <button type="submit" class="user-btn delete-btn">
			                Delete
			            </button>
			        </form>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="user-actions">
        <a class="user-btn back-btn"
           href="${pageContext.request.contextPath}/index">
            ← Back
        </a>

        <c:if test="${not empty sessionScope.loggedInUser}">
            <form action="${pageContext.request.contextPath}/logout"
                  method="post" class="d-inline">
                <button type="submit" class="user-btn logout-btn">
                    Logout
                </button>
            </form>
        </c:if>
    </div>

</div>

</body>
</html>
