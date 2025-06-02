<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Spare Part Terpopuler - Bengkelin</title>
</head>
<body>

<h1>Spare Part Terpopuler</h1>

<a href="${pageContext.request.contextPath}/sparepart?action=list">Kembali ke Daftar</a>

<c:if test="${not empty ATTR_TOTAL_USAGE}">
  <p>Total semua unit spare part yang pernah digunakan: <strong><c:out value="${totalUsage}" /></strong> unit.</p>
</c:if>

<table border="1" cellpadding="5" cellspacing="0">
  <thead>
  <tr>
    <th>Peringkat</th>
    <th>Kode Part</th>
    <th>Nama Spare Part</th>
    <th>Harga (Rp)</th>
    <th>Stok Saat Ini</th>
    <th>Total Digunakan</th>
  </tr>
  </thead>
  <tbody>
  <c:choose>
    <c:when test="${not empty sparePartList}">
      <c:forEach var="sp" items="${sparePartList}" varStatus="loop">
        <tr>
          <td>${loop.count}</td>
          <td><c:out value="${sp.kodePart}" /></td>
          <td><c:out value="${sp.nama}" /></td>
          <td><fmt:formatNumber value="${sp.harga}" type="number" minFractionDigits="0" maxFractionDigits="0" /></td>
          <td><c:out value="${sp.stok}" /></td>
          <td><c:out value="${sp.jumlahDigunakan}" /></td>
        </tr>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <tr>
        <td colspan="6" style="text-align: center;">Tidak ada data spare part populer.</td>
      </tr>
    </c:otherwise>
  </c:choose>
  </tbody>
</table>

<p style="text-align: center; font-size: small;">
  &copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App
</p>

</body>
</html>
