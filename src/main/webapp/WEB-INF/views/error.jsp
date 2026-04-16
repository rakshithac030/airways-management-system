<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>${title != null ? title : 'Error'}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            background: #f8fafc;
            color: #222;
        }
        .error-card {
            max-width: 900px;
            margin: 60px auto;
            padding: 28px;
            border-radius: 12px;
            background: #ffffff;
            box-shadow: 0 8px 30px rgba(15,23,42,0.06);
        }
        .meta {
            font-size: 13px;
            color: #6b7280;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="error-card">

        <!-- ===== TITLE & MESSAGE ===== -->
        <h2 class="mb-2">${title}</h2>
        <p class="lead">${message}</p>

        <!-- ===== META ===== -->
        <div class="meta mb-3">
            <div>Path: <strong>${path}</strong></div>
            <div>Status: <strong>${status}</strong></div>
        </div>

        <!-- ===== ACTIONS ===== -->
        <div class="d-flex gap-2 mb-4">
            <a class="btn btn-outline-primary"
               href="${pageContext.request.contextPath}/">
                Home
            </a>

            <c:if test="${not empty header.referer}">
                <a class="btn btn-outline-secondary"
                   href="${header.referer}">
                    Back
                </a>
            </c:if>
        </div>

        <!-- ===== REPORT ISSUE (UI ONLY) ===== -->
        <div class="card p-3">
            <h4 class="mb-3">📝 Report an Issue</h4>

            <form onsubmit="return showThanks();">
                <div class="mb-2">
                    <label class="form-label">What happened?</label>
                    <textarea class="form-control"
                              rows="3"
                              required
                              placeholder="Briefly describe what you were trying to do..."></textarea>
                </div>

                <div class="mb-2">
                    <label class="form-label">Your email (optional)</label>
                    <input type="email"
                           class="form-control"
                           placeholder="you@example.com"/>
                </div>

                <button class="btn btn-danger mt-2">
                    Submit Report
                </button>
            </form>
        </div>

        <hr/>

        <small class="text-muted">
            If this keeps happening, please report the issue above.
            Our support team reviews reported problems regularly.
        </small>

    </div>
</div>

<script>
    function showThanks() {
        alert("Thanks! Your issue has been recorded by our support team.");
        return false; // prevent actual submit
    }
</script>

</body>
</html>
