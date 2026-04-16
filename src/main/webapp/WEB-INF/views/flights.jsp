<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
<title>Flights</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/flights.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/app.css" />
</head>

<body class="bg-light">

	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

	<jsp:include page="navbar.jsp" />

	<div class="container mt-4">
		<!-- PAGE TITLE -->
		<c:choose>
			<c:when test="${mode == 'LIVE'}">
				<h2 class="mb-4 text-danger">
					<i class="bi bi-broadcast"></i> Live Flight Status
				</h2>
				<p class="text-muted">Real-time operational updates (no
					bookings)</p>
			</c:when>
			<c:otherwise>
				<h2 class="mb-4">
					<i class="bi bi-airplane"></i> Flights
				</h2>
			</c:otherwise>
		</c:choose>

		<c:if test="${not empty responseMessage}">
			<div class="alert alert-success">${responseMessage}</div>
		</c:if>

		<!-- ================= SEARCH ================= -->
		<c:if test="${mode != 'LIVE'}">
			<div class="card shadow-sm mb-4">
				<div class="card-header bg-primary text-white">
					<i class="bi bi-search"></i> Search Flights
				</div>

				<div class="card-body">
					<form action="${pageContext.request.contextPath}/flights/search"
						method="get" class="row g-3">

						<div class="col-md-5">
							<label class="form-label">Source</label> <select
								name="sourceAirportId" class="form-select" required>
								<option disabled selected>-- Select Source --</option>
								<c:forEach var="a" items="${airports}">
									<option value="${a.airportId}">${a.airportName}</option>
								</c:forEach>
							</select>
						</div>

						<div class="col-md-5">
							<label class="form-label">Destination</label> <select
								name="destinationAirportId" class="form-select" required>
								<option disabled selected>-- Select Destination --</option>
								<c:forEach var="a" items="${airports}">
									<option value="${a.airportId}">${a.airportName}</option>
								</c:forEach>
							</select>
						</div>

						<div class="col-md-2 d-flex align-items-end">
							<button class="btn btn-outline-primary w-100">Search</button>
						</div>
					</form>
				</div>
			</div>
		</c:if>

		<!-- ================= NO DIRECT FLIGHTS ================= -->
		<c:if test="${noDirectFlights}">
			<div class="alert alert-warning">❌ No direct flights available.
				Showing connecting flights.</div>
		</c:if>

		<!-- ================= CONNECTING FLIGHTS ================= -->
		<c:if test="${mode != 'LIVE' && not empty connectingFlights}">
			<div class="card shadow-sm mb-4">
				<div class="card-header bg-info text-white">🔁 Connecting
					Flight Options</div>

				<div class="card-body p-0">
					<table class="table table-striped mb-0">
						<thead>
							<tr>
								<th>Flight</th>
								<th>From</th>
								<th>To</th>
								<th>Departure</th>
								<th>Status</th>
								<th>Seats</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach var="conn" items="${connectingFlights}">
								<tr class="table-secondary">
									<td colspan="6">Via
										${conn.firstLeg.destinationAirport.airportName}</td>
								</tr>

								<tr>
									<td>${conn.firstLeg.flightCode}</td>
									<td>${conn.firstLeg.sourceAirport.airportName}</td>
									<td>${conn.firstLeg.destinationAirport.airportName}</td>
									<td><strong>${conn.firstLeg.departureTime.toLocalDate()}</strong><br />
										${conn.firstLeg.departureTime.toLocalTime()}</td>
									<td>${conn.firstLeg.status}</td>
									<td>${conn.firstLeg.availableSeats}</td>
								</tr>

								<tr>
									<td>${conn.secondLeg.flightCode}</td>
									<td>${conn.secondLeg.sourceAirport.airportName}</td>
									<td>${conn.secondLeg.destinationAirport.airportName}</td>
									<td><strong>${conn.secondLeg.departureTime.toLocalDate()}</strong><br />
										${conn.secondLeg.departureTime.toLocalTime()}</td>
									<td>${conn.secondLeg.status}</td>
									<td>${conn.secondLeg.availableSeats}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</c:if>

		<!-- ================= ADD FLIGHT (ADMIN) ================= -->
		<c:if
			test="${mode != 'LIVE' && empty noDirectFlights && sessionScope.role eq 'ADMIN'}">
			<div class="card shadow-sm mb-4">
				<div class="card-header bg-success text-white">
					<i class="bi bi-plus-circle"></i> Add Flight
				</div>

				<div class="card-body">
					<form action="${pageContext.request.contextPath}/flights/create"
						method="post" class="row g-3">

						<div class="col-md-3">
							<label>Flight Code</label> <input class="form-control"
								name="flightCode" required />
						</div>

						<div class="col-md-3">
							<label>Source</label> <select name="sourceAirportId"
								class="form-select" required>
								<c:forEach var="a" items="${airports}">
									<option value="${a.airportId}">${a.airportName}</option>
								</c:forEach>
							</select>
						</div>

						<div class="col-md-3">
							<label>Destination</label> <select name="destinationAirportId"
								class="form-select" required>
								<c:forEach var="a" items="${airports}">
									<option value="${a.airportId}">${a.airportName}</option>
								</c:forEach>
							</select>
						</div>

						<div class="col-md-3">
							<label>Departure Time</label> <input type="datetime-local"
								name="departureTime" min="${now}" class="form-control" required />
						</div>

						<div class="col-12">
							<button class="btn btn-success">Add Flight</button>
						</div>
					</form>
				</div>
			</div>
		</c:if>

		<!-- ================= AVAILABLE FLIGHTS ================= -->
		<c:if test="${empty noDirectFlights}">
	    <div class="card shadow-sm">
	        <div class="card-header ${mode == 'LIVE' ? 'bg-dark' : 'bg-secondary'} text-white">
	            <c:choose>
	                <c:when test="${mode == 'LIVE'}">
	                    <i class="bi bi-broadcast"></i> Live Operational Status
	                </c:when>
	                <c:otherwise>
	                    <i class="bi bi-list-ul"></i> Available Flights
	                </c:otherwise>
	            </c:choose>
	        </div>
	
	        <div class="card-body p-0">
	            <table class="table table-striped table-hover mb-0">
	                <thead>
	                    <tr>
	                        <th>Code</th>
	                        <th>From</th>
	                        <th>To</th>
	                        <th>Departure</th>
	                        <th>Status</th>
	                        <c:if test="${mode != 'LIVE'}">
	                            <th>Seats</th>
	                            <th>Fare</th>
	                            <th>Actions</th>
	                        </c:if>
	                    </tr>
	                </thead>
	
	                <tbody>
	                    <c:forEach var="f" items="${flights}">
	                        <tr class="${f.flightId == updatedFlightId ? 'updated-row' : ''} ${f.status == 'CANCELLED' ? 'cancelled-row' : ''} ${f.status == 'DELAYED' ? 'delayed-row' : ''}">
	                            <td>${f.flightCode}</td>
	                            <td>${f.sourceAirport.airportName}</td>
	                            <td>${f.destinationAirport.airportName}</td>
	                            <td><strong>${f.departureTime.toLocalDate()}</strong><br />${f.departureTime.toLocalTime()}</td>
	                            <td>
	                                <span class="badge ${f.status == 'CANCELLED' ? 'bg-danger' : f.status == 'DELAYED' ? 'bg-warning text-dark' : f.status == 'SCHEDULED' ? 'bg-success' : 'bg-secondary'}">
	                                    ${f.status}
	                                </span>
	                            </td>
	                            <c:if test="${mode != 'LIVE'}">
	                                <td>${f.availableSeats}</td>
	                                <td>₹ ${f.baseFare}</td>
	                                <td>
	                                    <c:choose>
	                                        <c:when test="${sessionScope.role eq 'ADMIN' && !f.departed}">
	                                            <form action="${pageContext.request.contextPath}/flights/cancel" method="post" style="display: inline;">
	                                                <input type="hidden" name="flightId" value="${f.flightId}" />
	                                                <button class="btn btn-sm btn-warning">Cancel</button>
	                                            </form>
	                                            <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#editFlight${f.flightId}">Edit</button>
	                                            <form action="${pageContext.request.contextPath}/flights/delete" method="post" style="display: inline;">
	                                                <input type="hidden" name="flightId" value="${f.flightId}" />
	                                                <button class="btn btn-sm btn-danger">Delete</button>
	                                            </form>
	                                            
												<c:if test="${f.availableSeats > 0 && not empty sessionScope.loggedInUser && f.futureFlight}">
											        <form action="${pageContext.request.contextPath}/booking/create"
											              method="post"
											              class="d-flex gap-1 mt-1">
											            <input type="hidden" name="flightId" value="${f.flightId}" />
											            <input type="number" name="seats" min="1" max="${f.availableSeats}"
											                   class="form-control form-control-sm" style="width: 70px" required />
											            <button class="btn btn-sm btn-success">Proceed</button>
											        </form>
											    </c:if>
	                                            
	                                            
	                                        </c:when>
	                                        <c:when test="${sessionScope.role eq 'ADMIN' && f.departed}">
	                                            <span class="text-muted fw-semibold">Read-only</span>
	                                        </c:when>
	                                        <c:otherwise>
	                                            <c:if test="${f.departed}">
	                                                <span class="text-muted">Departed</span>
	                                            </c:if>
	                                            <c:if test="${!f.departed && f.availableSeats == 0}">
	                                                <span class="text-danger fw-semibold">Full</span>
	                                            </c:if>
	                                            <c:if test="${!f.departed && f.availableSeats > 0 && empty sessionScope.loggedInUser}">
	                                                <a href="${pageContext.request.contextPath}/login?redirect=/flights/view" class="btn btn-sm btn-outline-primary"> Login to book </a>
	                                            </c:if>
	                                            <c:if test="${!f.departed && f.availableSeats > 0 && not empty sessionScope.loggedInUser && f.futureFlight}">
	                                                <form action="${pageContext.request.contextPath}/booking/create" method="post" class="d-flex gap-1">
	                                                    <input type="hidden" name="flightId" value="${f.flightId}" />
	                                                    <input type="number" name="seats" min="1" max="${f.availableSeats}" class="form-control form-control-sm" style="width: 70px" required />
	                                                    <button class="btn btn-sm btn-success">Proceed</button>
	                                                </form>
	                                            </c:if>
	                                        </c:otherwise>
	                                    </c:choose>
	                                </td>
	                            </c:if>
	                        </tr>
	                    </c:forEach>
	                </tbody>
	            </table>
	        </div>
	    </div>
	</c:if>
		<c:if test="${empty flights && empty connectingFlights}">
		    <div class="alert alert-info mt-3">
		        No flights found for the selected criteria.
		    </div>
		</c:if>


		<!-- ================= PAGINATION ================= -->
		<c:if test="${mode != 'LIVE' && empty noDirectFlights}">
			<div class="d-flex justify-content-between mt-3">

				<c:if test="${page > 0}">
					<a class="btn btn-outline-primary"
						href="${pageContext.request.contextPath}/flights/view?page=${page - 1}">
						⬅ Previous </a>
				</c:if>
				<c:if test="${not empty flights and fn:length(flights) >= 5}">
					<a class="btn btn-outline-primary ms-auto"
						href="${pageContext.request.contextPath}/flights/view?page=${page + 1}">
						Next ➡ </a>
				</c:if>

			</div>
		</c:if>

		<!-- ================= EDIT MODALS ================= -->
		<c:if test="${sessionScope.role eq 'ADMIN'}">
			<c:forEach var="f" items="${flights}">
				<div class="modal fade" id="editFlight${f.flightId}" tabindex="-1">
					<div class="modal-dialog">
						<form action="${pageContext.request.contextPath}/flights/update"
							method="post" class="modal-content">
							<div class="modal-header">
								<h5>Edit Flight ${f.flightCode}</h5>
								<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
							</div>
							<div class="modal-body">
								<input type="hidden" name="flightId" value="${f.flightId}" /> <label>Departure
									Time</label> <input type="datetime-local" name="departureTime"
									class="form-control" required /> <label class="mt-2">Status</label>
								<select name="status" class="form-select">
									<c:forEach var="st" items="${flightStatuses}">
										<option value="${st}"
											<c:if test="${st == f.status}">selected</c:if>>
											${st}</option>
									</c:forEach>
								</select>
							</div>
							<div class="modal-footer">
								<button class="btn btn-success">Update</button>
							</div>
						</form>
					</div>
				</div>
			</c:forEach>
		</c:if>
	</div>
</body>
</html>
