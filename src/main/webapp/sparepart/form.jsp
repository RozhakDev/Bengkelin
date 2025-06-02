<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>
        <c:choose>
            <c:when test="${not empty sparePart && not empty sparePart.kodePart}">Edit Spare Part</c:when>
            <c:otherwise>Tambah Spare Part Baru</c:otherwise>
        </c:choose>
        - Bengkelin
    </title>
</head>
<body>
<h1>
    <c:choose>
        <c:when test="${not empty sparePart && not empty sparePart.kodePart}">Edit Data Spare Part</c:when>
        <c:otherwise>Tambah Spare Part Baru</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty errorMessage}">
    <div style="color: red; border: 1px solid red; padding: 10px; margin-bottom: 10px;">
        <strong>Error:</strong> <c:out value="${errorMessage}" />
    </div>
</c:if>

<form action="${formActionUrl}" method="post">
    <div>
        <label for="kodePart">Kode Part</label><br>
        <input type="text" id="kodePart" name="kodePart"
               value="<c:out value='${sparePart.kodePart}' />"
               <c:if test="${not empty sparePart.kodePart && formActionUrl.contains('update')}">readonly</c:if>
               required>
        <c:if test="${not empty sparePart.kodePart && formActionUrl.contains('update')}">
            <div style="font-size: small; color: gray;">Kode part tidak dapat diubah setelah dibuat.</div>
        </c:if>
    </div>
    <br>

    <div>
        <label for="nama">Nama Spare Part</label><br>
        <input type="text" id="nama" name="nama" value="<c:out value='${sparePart.nama}' />" required>
    </div>
    <br>

    <div>
        <label for="harga">Harga (Rp)</label><br>
        <input type="number" id="harga" name="harga" step="100" min="0" value="<c:out value='${sparePart.harga}' />" required>
    </div>
    <br>

    <div>
        <label for="stok">Stok</label><br>
        <input type="number" id="stok" name="stok" min="0" value="<c:out value='${sparePart.stok}' />" required>
    </div>
    <br>

    <div>
        <button type="submit">
            <c:choose>
                <c:when test='${formActionUrl.contains("update")}'>Simpan Perubahan</c:when>
                <c:otherwise>Tambah Spare Part</c:otherwise>
            </c:choose>
        </button>
        &nbsp;
        <a href="${pageContext.request.contextPath}/sparepart?action=list">Batal</a>
    </div>
</form>

<footer style="margin-top: 20px; font-size: small; color: gray;">
    &copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App. All rights reserved.
</footer>
</body>
</html>
