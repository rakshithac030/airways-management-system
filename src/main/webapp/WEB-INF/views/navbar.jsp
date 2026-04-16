<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar">

    <a class="nav-item" href="${pageContext.request.contextPath}/dashboard">
         Home
    </a>

    <a class="nav-item" href="${pageContext.request.contextPath}/flights/view">
         Flights
    </a>

    <c:if test="${not empty sessionScope.loggedInUser}">

        <a class="nav-item" href="${pageContext.request.contextPath}/booking/myBookings">
             My Bookings
        </a>
            <a href="${pageContext.request.contextPath}/airport" class="nav-item">
         Airports
    </a>
        
    </c:if>

    <!-- ADMIN ONLY -->
    <c:if test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role eq 'ADMIN'}">

        <a class="nav-item" href="${pageContext.request.contextPath}/booking/all">
             All Bookings
        </a>

        <a class="nav-item" href="${pageContext.request.contextPath}/admin/dashboard">
             Admin Dashboard
        </a>

    </c:if>

    <div class="nav-right">
        <c:if test="${not empty sessionScope.loggedInUser}">
            <span class="nav-user">
                👤 ${sessionScope.loggedInUser.username}
            </span>

            <a class="nav-item logout"
               href="${pageContext.request.contextPath}/logout">
                ⏻ Logout
            </a>
        </c:if>
    </div>

</nav>
