<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Booking Summary</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-5">
    <div class="card shadow">
        <div class="card-header bg-primary text-white">
            Booking Summary
        </div>

        <div class="card-body">
            <p><strong>Flight:</strong> ${flight.flightCode}</p>

            <p><strong>Route:</strong>
                ${flight.sourceAirport.airportName}
                →
                ${flight.destinationAirport.airportName}
            </p>

            <p><strong>Departure:</strong>
                ${flight.departureTime.toLocalDate()}
                ${flight.departureTime.toLocalTime()}
            </p>
            

            <p><strong>Seats:</strong> ${seats}</p>
            
            <div class="mt-3">
                <p><strong>Total Fare: </strong>
                    <c:choose>
                        <c:when test="${hoursToDeparture < 6}">
                            <span class="text-danger">
                                ₹ <fmt:formatNumber value="${flight.emergencyFare * seats}" type="number" maxFractionDigits="2" /> 
                                (Emergency fare)
                            </span>
                        </c:when>
                        <c:otherwise>
                            ₹ <fmt:formatNumber value="${flight.baseFare * seats}" type="number" maxFractionDigits="2" />
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>

            <form action="${pageContext.request.contextPath}/booking/create-final" method="post">
                <input type="hidden" name="flightId" value="${flight.flightId}" />
                <input type="hidden" name="seats" value="${seats}" />
                
                <button class="btn btn-success">
                    Confirm Booking
                </button>

                <a href="${pageContext.request.contextPath}/flights/view" class="btn btn-secondary ms-2">
                    Cancel
                </a>
            </form>
        </div>
    </div>
</div>

</body>
</html>