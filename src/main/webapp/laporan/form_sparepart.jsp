<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Form Laporan Pelanggan per Spare Part - Bengkelin</title>
</head>
<body>

<h1>Laporan Pelanggan per Spare Part</h1>
<p>Pilih spare part untuk melihat daftar pelanggan yang menggunakannya.</p>

<c:if test="${not empty errorMessage}">
    <div style="border: 1px solid red; padding: 10px; margin-bottom: 15px;">
        <strong>Error:</strong> <c:out value="${errorMessage}" />
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/laporan" method="post">
    <input type="hidden" name="action" value="hasilSparePart">

    <label for="kodePart">Pilih Spare Part:</label><br>
    <select id="kodePart" name="kodePart" required>
        <option value="">-- Pilih Spare Part --</option>
        <c:forEach var="sp" items="${sparePartList}">
            <option value="${sp.kodePart}" <c:if test="${param.kodePart == sp.kodePart}">selected</c:if>>
                    ${sp.nama} (<c:out value="${sp.kodePart}" />)
            </option>
        </c:forEach>
    </select>
    <br><br>

    <a href="${pageContext.request.contextPath}/laporan?action=menu">Kembali ke Menu Laporan</a>
    <input type="submit" value="Tampilkan Laporan">
</form>

<br><br>
<hr>
<footer>
    <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App. All rights reserved.</p>
</footer>

</body>
</html>
