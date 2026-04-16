<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.loggedInUser and sessionScope.role ne 'ADMIN'}">
    <c:redirect url="/login"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <title>My Bookings</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/bookings.css"/>
        
    <link rel="stylesheet"
      	  href="${pageContext.request.contextPath}/resources/css/app.css"/>
    
</head>

<body class="bg-light">

<jsp:include page="navbar.jsp"/>

<div class="container mt-4">

    <h2 class="mb-3">
        <c:choose>
            <c:when test="${sessionScope.role eq 'ADMIN'}">
                ✈ All Flight Bookings
            </c:when>
            <c:otherwise>
                ✈ My Flight Bookings
            </c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty responseMessage}">
        <div class="alert alert-info">
            ${responseMessage}
        </div>
    </c:if>

    <table class="bookings-table">
        <thead>
        <tr>
            <th>Booking ID</th>

            <c:if test="${sessionScope.role eq 'ADMIN'}">
                <th>User</th>
            </c:if>

            <th>Flight Code</th>
            <th>From</th>
            <th>To</th>
            <th>Seats</th>
            <th>Status</th>
            <th>Fare</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="b" items="${Bookings}">
            <tr>
                <td>${b.bookingId}</td>

                <c:if test="${sessionScope.role eq 'ADMIN'}">
                    <td>
                        ${b.user.username}<br/>
                        <small class="text-muted">${b.user.email}</small>
                    </td>
                </c:if>

                <td>${b.flight.flightCode}</td>
                <td>${b.flight.sourceAirport.airportName}</td>
                <td>${b.flight.destinationAirport.airportName}</td>
                <td>
				    <strong>${b.flight.departureTime.toLocalDate()}</strong><br/>
				    <small class="text-muted">
				        ${b.flight.departureTime.toLocalTime()}
				    </small>
				</td>
                <td>${b.seatsBooked}</td>
				<td>
				    ₹ ${b.appliedFare}
				    <c:set var="emergencyTotalFare" value="${b.flight.emergencyFare * b.seatsBooked}" />
				    <c:if test="${b.appliedFare == emergencyTotalFare}">
				        <br/>
				        <small class="text-danger">Emergency fare applied</small>
				    </c:if>
				</td>
	
                <td class="${b.status}">
                    ${b.status}
                </td>

                <td>
                    <!-- CONFIRM -->
                    <c:if test="${b.status == 'PENDING'}">
                        <form action="${pageContext.request.contextPath}/booking/confirm"
                              method="post" class="d-inline">
                            <input type="hidden" name="bookingId" value="${b.bookingId}"/>
                            <button class="booking-btn confirm-btn">
                                Confirm
                            </button>
                        </form>
                    </c:if>

                    <!-- CANCEL -->
                    <c:if test="${b.status != 'CANCELLED' && b.cancellable}">
                        <form action="${pageContext.request.contextPath}/booking/cancel"
                              method="post" class="d-inline">
                            <input type="hidden" name="bookingId" value="${b.bookingId}"/>
                            <button class="booking-btn cancel-btn">
                                Cancel
                            </button>
                        </form>
                    </c:if>
                    
                    <c:if test="${sessionScope.role eq 'ADMIN'}">
				        <form action="${pageContext.request.contextPath}/booking/admin/cancel"
				              method="post" class="d-inline">
				            <input type="hidden" name="bookingId" value="${b.bookingId}"/>
				            <button class="booking-btn cancel-btn">
				                Force Cancel
				            </button>
				        </form>
				    </c:if>
                    
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="mt-4">
        <a class="btn btn-secondary"
           href="${pageContext.request.contextPath}/flights/view">
            ⬅ Back to Flights
        </a>
    </div>

</div>

</body>
</html>
