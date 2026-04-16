<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.loggedInUser}">
    <c:redirect url="/login"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/app.css"/>
</head>

<body class="bg-light">

<jsp:include page="navbar.jsp"/>

<div class="container mt-4">

    <h2 class="mb-4">
        <i class="bi bi-speedometer2"></i> Admin Dashboard
    </h2>

    <!-- METRICS ROW -->
    <div class="row g-3">

        <div class="col-md-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <h6>Total Users</h6>
                    <h3>${totalUsers}</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <h6>Total Airports</h6>
                    <h3>${totalAirports}</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <h6>Total Flights</h6>
                    <h3>${totalFlights}</h3>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm text-center">
                <div class="card-body">
                    <h6>Total Bookings</h6>
                    <h3>${totalBookings}</h3>
                </div>
            </div>
        </div>
    </div>

    <!-- STATUS BREAKDOWN -->
    <div class="row g-3 mt-3">

        <div class="col-md-4">
            <div class="card border-success shadow-sm">
                <div class="card-body text-center">
                    <h6>Confirmed Bookings</h6>
                    <h3 class="text-success">${confirmedBookings}</h3>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card border-warning shadow-sm">
                <div class="card-body text-center">
                    <h6>Pending Bookings</h6>
                    <h3 class="text-warning">${pendingBookings}</h3>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card border-danger shadow-sm">
                <div class="card-body text-center">
                    <h6>Cancelled Bookings</h6>
                    <h3 class="text-danger">${cancelledBookings}</h3>
                </div>
            </div>
        </div>
    </div>

    <!-- OPERATIONAL INSIGHTS -->
    <div class="row g-3 mt-3">

        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <h6>Seats Sold Today</h6>
                    <h3>${seatsSoldToday}</h3>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <h6>Flights Departing Today</h6>
                    <h3>${flightsDepartingToday}</h3>
                </div>
            </div>
        </div>
    </div>

</div>

</body>
</html>
