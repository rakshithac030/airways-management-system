<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${sessionScope.role ne 'ADMIN'}">
    <c:redirect url="${pageContext.request.contextPath}/dashboard"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <title>Runways</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/app.css"/>
</head>

<body class="bg-light">

<jsp:include page="navbar.jsp"/>

<div class="container mt-4">

    <h2>Runway Management</h2>

    <!-- MESSAGE -->
    <c:if test="${not empty responseMessage}">
        <div class="alert alert-info">${responseMessage}</div>
    </c:if>

    <!-- ================= ADD / UPDATE FORM ================= -->

    <form action="${pageContext.request.contextPath}/runways/${not empty editRunway ? 'update' : 'create'}"
          method="post" class="mb-4">

        <!-- METHOD OVERRIDE -->
        <c:if test="${not empty editRunway}">
            <input type="hidden" name="_method" value="POST"/>
            <input type="hidden" name="runwayId" value="${editRunway.runwayId}"/>
        </c:if>

        <input type="hidden" name="airportId" value="${AirportId}" />

        <input name="runwayNumber"
               placeholder="Runway Code"
               value="${editRunway.runwayNumber}"
               required />

        <input name="length"
               type="number"
               placeholder="Length"
               value="${editRunway.length}"
               required />

        <input name="surfaceType"
               placeholder="Surface Type"
               value="${editRunway.surfaceType}" />

        <button class="btn btn-success">
            ${not empty editRunway ? 'Update Runway' : 'Add Runway'}
        </button>
    </form>

    <!-- ================= RUNWAY TABLE ================= -->

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Code</th>
            <th>Length</th>
            <th>Surface</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>

        <!-- EMPTY -->
        <c:if test="${empty Runways}">
            <tr>
                <td colspan="5" class="text-center">No runways found</td>
            </tr>
        </c:if>

        <!-- DATA -->
        <c:forEach var="r" items="${Runways}">
            <tr>
                <td>${r.runwayId}</td>
                <td>${r.runwayNumber}</td>
                <td>${r.length}</td>
                <td>${r.surfaceType}</td>

                <td>

                    <!-- EDIT -->
                    <form action="${pageContext.request.contextPath}/runways/edit"
                          method="post" style="display:inline;">
                        <input type="hidden" name="_method" value="POST"/>
                        <input type="hidden" name="runwayId" value="${r.runwayId}" />
                        <input type="hidden" name="airportId" value="${AirportId}" />

                        <button class="btn btn-warning btn-sm">Edit</button>
                    </form>

                    <!-- DELETE -->
                    <form action="${pageContext.request.contextPath}/runways/delete"
                          method="post" style="display:inline;">

                        <input type="hidden" name="_method" value="POST"/>
                        <input type="hidden" name="runwayId" value="${r.runwayId}" />
                        <input type="hidden" name="airportId" value="${AirportId}" />

                        <button class="btn btn-danger btn-sm"
                                onclick="return confirm('Delete runway?')">
                            Delete
                        </button>
                    </form>

                </td>
            </tr>
        </c:forEach>

        </tbody>
    </table>

</div>

</body>
</html>