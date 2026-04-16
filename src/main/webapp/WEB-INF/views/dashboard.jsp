<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Airways</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/app.css"/>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/dashboard.css"/>
</head>

<body>

<!-- ================= NAVBAR ================= -->
<jsp:include page="navbar.jsp"/>

<!-- ================= HERO ================= -->
<section class="hero hero-premium">
    <div class="hero-overlay"></div>

    <div class="hero-inner">

        <h1>Travel begins with clarity.</h1>

        <p class="hero-subtitle">
            Real-time flight intelligence, operational visibility,
            and travel tools built for confidence.
        </p>

    	<c:if test="${not empty sessionScope.loggedInUser}">
            <div class="welcome-user">
                Welcome back, ${sessionScope.loggedInUser.username}
                <span>Your bookings and travel tools — unified.</span>
            </div>
        </c:if>
        
        <div class="hero-cta">
            <a href="${pageContext.request.contextPath}/flights/view"
               class="cta-primary">
                Explore flights
            </a>

            <a href="${pageContext.request.contextPath}/flights/view?mode=LIVE"
               class="cta-ghost">
                Live flight status
            </a>
        </div>
    </div>
</section>

<!-- ================= STATS PANEL ================= -->
<section class="ops-panel">

    <a href="${pageContext.request.contextPath}/flights/view?status=SCHEDULED"
       class="ops-item">
        <span class="ops-label">Active flights</span>
        <span class="ops-value">${scheduledFlights}</span>
    </a>

    <a href="${pageContext.request.contextPath}/flights/view?status=DELAYED"
       class="ops-item warn">
        <span class="ops-label">Delayed</span>
        <span class="ops-value">${delayedFlights}</span>
    </a>

    <a href="${pageContext.request.contextPath}/flights/view?status=CANCELLED"
       class="ops-item danger">
        <span class="ops-label">Cancelled</span>
        <span class="ops-value">${cancelledFlights}</span>
    </a>

    <c:if test="${not empty sessionScope.loggedInUser}">
        <a href="${pageContext.request.contextPath}/booking/myBookings"
           class="ops-item neutral">
            <span class="ops-label">My trips</span>
            <span class="ops-value">${myBookingsCount}</span>
        </a>
    </c:if>

</section>



<!-- ================= MAIN CONTENT ================= -->
<main class="content">

    <!-- ===== CONTEXT HEADER ===== -->
    <header class="section-header">
                <h2>Explore the Airways network</h2>
                <p>Plan ahead, stay informed, and travel smarter.</p>
    </header>

    <!-- ================= QUICK ACTIONS ================= -->
    <section class="quick-actions premium">

        <a href="${pageContext.request.contextPath}/flights/view"
           class="action-card">
            ✈ Flights
            <span>Routes, schedules, and availability</span>
        </a>

        <a href="${pageContext.request.contextPath}/airport"
           class="action-card">
            🏢 Airports
            <span>Terminal and airport information</span>
        </a>

        <c:if test="${empty sessionScope.loggedInUser}">
            <a href="${pageContext.request.contextPath}/login"
               class="action-card highlight">
                🔐 Sign in
                <span>Bookings, alerts, and updates</span>
            </a>
        </c:if>

        <c:if test="${not empty sessionScope.loggedInUser}">
            <a href="${pageContext.request.contextPath}/booking/myBookings"
               class="action-card">
                📄 My trips
                <span>View and manage reservations</span>
            </a>
        </c:if>

    </section>

    <!-- ================= SUPPORT ================= -->
    <section class="support-panel">
        <h3>Need assistance?</h3>
        <p>Our support team is here to help you move forward.</p>

        <p class="support-email">support@airwaysapp.com</p>
    </section>

</main>

<!-- ================= FOOTER ================= -->
<footer class="system-footer">
    <span class="status-dot"></span>
    System operational
    <span class="separator">|</span>
    Updated <span id="lastUpdated"></span>
</footer>

<script>
    document.getElementById("lastUpdated").innerText =
        new Date().toLocaleString();
</script>

</body>
</html>
