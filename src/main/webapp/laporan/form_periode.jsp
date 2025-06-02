<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Form Laporan Transaksi per Periode - Bengkelin</title>
</head>
<body>

<h2>Laporan Transaksi per Periode</h2>
<p>Pilih rentang tanggal untuk melihat transaksi.</p>

<c:if test="${not empty errorMessage}">
    <div style="background-color:#fdd; border:1px solid red; padding:10px; margin-bottom:10px;">
        <strong>Error:</strong> <c:out value="${errorMessage}" />
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/laporan" method="post">
    <input type="hidden" name="action" value="hasilPeriode">

    <label for="startDate">Tanggal Mulai:</label><br>
    <input type="date" id="startDate" name="startDate"
           value="<c:out value='${param.startDate}'/>" required>
    <br><br>

    <label for="endDate">Tanggal Akhir:</label><br>
    <input type="date" id="endDate" name="endDate"
           value="<c:out value='${param.endDate}'/>" required>
    <br><br>

    <a href="${pageContext.request.contextPath}/laporan?action=menu">Kembali ke Menu Laporan</a>
    <input type="submit" value="Tampilkan Laporan">
</form>

<br><hr><br>

<footer>
    <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App.</p>
</footer>

</body>
</html>
