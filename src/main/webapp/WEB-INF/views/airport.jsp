<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Airports</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet">
	<link rel="stylesheet"
      	  href="${pageContext.request.contextPath}/resources/css/app.css"/>

    <!-- Airport page CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/airport.css"/>
          
</head>

<body class="bg-light">

<jsp:include page="navbar.jsp"/>

<!-- ================= PAGE CONTENT ================= -->
<div class="airport-page">

    <!-- ================= SEARCH ================= -->
    <div class="card shadow-sm mb-4">
        <div class="card-header">
            <i class="bi bi-search"></i> Search Airports
        </div>

        <div class="card-body">

            <!-- ADMIN: Search by ID -->
            <c:if test="${sessionScope.role == 'ADMIN'}">
                <form action="${pageContext.request.contextPath}/airport/searchById"
                      method="get" class="row g-3 mb-3">
                    <div class="col-md-6">
                        <input class="form-control"
                               name="airportId"
                               placeholder="Search by Airport ID">
                    </div>
                    <div class="col-md-6">
                        <button class="btn btn-outline-primary w-100">
                            Search by ID
                        </button>
                    </div>
                </form>
            </c:if>

            <!-- ALL USERS -->
            <form action="${pageContext.request.contextPath}/airport/searchByName"
                  method="get" class="row g-3">
                <div class="col-md-5">
                    <input class="form-control"
                           name="airportName"
                           placeholder="Airport Name" required>
                </div>
                <div class="col-md-5">
                    <input class="form-control"
                           name="airportLocation"
                           placeholder="Location" required>
                </div>
                <div class="col-md-2">
                    <button class="btn btn-outline-primary w-100">
                        Search
                    </button>
                </div>
            </form>
            
            

            <c:if test="${not empty responseMessage}">
                <div class="alert alert-danger mt-3">
                    ${responseMessage}
                </div>
            </c:if>

        </div>
    </div>
    
		<c:if test="${fn:length(Airports) == 1}">
		    <div class="alert alert-info mb-3 d-flex align-items-center gap-2">
		        <i class="bi bi-info-circle"></i>
		        Showing search result
		        <a href="${pageContext.request.contextPath}/airport"
		           class="ms-auto btn btn-sm btn-outline-secondary">
		            Clear search
		        </a>
		    </div>
		</c:if>


    <!-- ================= AIRPORT LIST ================= -->
    <div class="card shadow-sm">
        <div class="card-header">
            <i class="bi bi-list-ul"></i> Available Airports
        </div>

        <div class="card-body p-0">
            <table class="table table-striped table-hover mb-0 align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Location</th>
                    <th>Parking Capacity</th>
                    <th>Status</th>
                    <<c:if test="${sessionScope.role eq 'ADMIN'}">
    					<th>Runways</th>
					</c:if>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="a" items="${Airports}">
                    <tr >
                        <td>${a.airportId}</td>
                        <td>${a.airportName}</td>
                        <td>${a.airportLocation}</td>
                        <td class="capacity">${a.parkingCapacity} slots</td>

                        <td>
                            <c:choose>
                                <c:when test="${sessionScope.role == 'ADMIN'}">
                                    <form action="${pageContext.request.contextPath}/airport/updateStatus"
                                          method="post" class="d-flex gap-2">
                                        <input type="hidden"
                                               name="airportId"
                                               value="${a.airportId}"/>

                                        <select name="status"
                                                class="form-select form-select-sm">
                                            <c:forEach var="s" items="${airportStatuses}">
                                                <option value="${s}"
                                                    <c:if test="${a.status == s}">selected</c:if>>
                                                    ${s}
                                                </option>
                                            </c:forEach>
                                        </select>

                                        <button class="btn btn-sm btn-primary">
                                            Update
                                        </button>
                                    </form>
                                </c:when>

                                <c:otherwise>
                                    <span class="badge
                                        ${a.status == 'ACTIVE' ? 'bg-success' :
                                          a.status == 'UNDER_MAINTENANCE' ? 'bg-warning text-dark' :
                                          'bg-danger'}">
                                        ${a.status}
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>

						<c:if test="${sessionScope.role eq 'ADMIN'}">
						    <td>
						        <a class="btn btn-sm btn-outline-primary"
						           href="${pageContext.request.contextPath}/runways/airportRunways?airportId=${a.airportId}">
						            <i class="bi bi-diagram-3"></i> Runways
						        </a>
						    </td>
						</c:if>

                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</div>

</body>
</html>
