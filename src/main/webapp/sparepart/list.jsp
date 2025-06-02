<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Daftar Spare Part - Bengkelin</title>
</head>
<body>
<h1>Manajemen Data Spare Part</h1>

<div>
    <a href="${pageContext.request.contextPath}/sparepart?action=new">Tambah Spare Part</a> |
    <a href="${pageContext.request.contextPath}/sparepart?action=popular">Lihat Populer</a>
</div>

<form action="${pageContext.request.contextPath}/sparepart" method="get">
    <input type="hidden" name="action" value="search">
    <input type="text" name="searchKeyword" value="<c:out value='${searchKeyword}'/>" placeholder="Cari...">
    <button type="submit">Cari</button>
    <c:if test="${not empty searchKeyword}">
        <a href="${pageContext.request.contextPath}/sparepart?action=list">Reset</a>
    </c:if>
</form>

<c:if test="${not empty param.successMessage}">
    <div style="color: green;">
        <c:choose>
            <c:when test="${param.successMessage == 'SparePartAdded'}">Spare part berhasil ditambahkan.</c:when>
            <c:when test="${param.successMessage == 'SparePartUpdated'}">Data spare part berhasil diperbarui.</c:when>
            <c:when test="${param.successMessage == 'SparePartDeleted'}">Spare part berhasil dihapus.</c:when>
            <c:otherwise>Operasi berhasil.</c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${not empty param.errorMessage}">
    <div style="color: red;">${param.errorMessage}</div>
</c:if>

<table border="1" cellpadding="5" cellspacing="0">
    <tr>
        <th>Kode Part</th>
        <th>Nama</th>
        <th>Harga (Rp)</th>
        <th>Stok</th>
        <th>Digunakan</th>
        <th>Aksi</th>
    </tr>
    <c:choose>
        <c:when test="${not empty sparePartList}">
            <c:forEach var="sp" items="${sparePartList}">
                <tr>
                    <td><c:out value="${sp.kodePart}" /></td>
                    <td><c:out value="${sp.nama}" /></td>
                    <td><fmt:formatNumber value="${sp.harga}" type="number" minFractionDigits="0" maxFractionDigits="0" /></td>
                    <td><c:out value="${sp.stok}" /></td>
                    <td><c:out value="${sp.jumlahDigunakan}" /></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/sparepart?action=edit&kodePart=${sp.kodePart}">Edit</a> |
                        <a href="${pageContext.request.contextPath}/sparepart?action=delete&kodePart=${sp.kodePart}" onclick="return confirm('Yakin hapus ${sp.nama}?');">Hapus</a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="6">Tidak ada data spare part ditemukan.
                    <c:if test="${not empty searchKeyword}">
                        (Keyword: "<c:out value='${searchKeyword}'/>")
                    </c:if>
                </td>
            </tr>
        </c:otherwise>
    </c:choose>
</table>

<div style="margin-top: 20px; font-size: small;">
    &copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App
</div>
</body>
</html>
