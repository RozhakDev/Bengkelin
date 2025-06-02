<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Daftar Transaksi - Bengkelin</title>
</head>
<body>
<h1>Manajemen Data Transaksi</h1>

<a href="${pageContext.request.contextPath}/transaksi?action=new">Tambah Transaksi Baru</a>

<form action="${pageContext.request.contextPath}/transaksi" method="get">
    <input type="hidden" name="action" value="list">
    <label>
        Tanggal Mulai:
        <input type="date" name="startDate" value="<c:out value='${startDate}'/>">
    </label>
    <label>
        Tanggal Akhir:
        <input type="date" name="endDate" value="<c:out value='${endDate}'/>">
    </label>
    <button type="submit">Filter</button>
    <c:if test="${not empty startDate || not empty endDate}">
        <a href="${pageContext.request.contextPath}/transaksi?action=list">Reset Filter</a>
    </c:if>
</form>

<c:if test="${not empty param.successMessage}">
    <p style="color: green;">
        <c:choose>
            <c:when test="${param.successMessage == 'TransaksiBerhasilDitambahkan'}">Transaksi berhasil ditambahkan.</c:when>
            <c:when test="${param.successMessage == 'TransaksiBerhasilDihapus'}">Transaksi berhasil dihapus.</c:when>
            <c:otherwise>Operasi berhasil.</c:otherwise>
        </c:choose>
    </p>
</c:if>
<c:if test="${not empty errorMessage}">
    <p style="color: red;">${errorMessage}</p>
</c:if>
<c:if test="${not empty param.errorMessage && empty errorMessage}">
    <p style="color: red;">${param.errorMessage}</p>
</c:if>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
    <tr>
        <th>No. Transaksi</th>
        <th>Tanggal</th>
        <th>ID Pelanggan</th>
        <th>Nama Pelanggan</th>
        <th>Biaya Service (Rp)</th>
        <th>Total Biaya (Rp)</th>
        <th>Aksi</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${not empty transaksiList}">
            <c:forEach var="trx" items="${transaksiList}">
                <tr>
                    <td><c:out value="${trx.noTransaksi}" /></td>
                    <td><fmt:formatDate value="${trx.tanggal}" pattern="dd-MM-yyyy HH:mm" /></td>
                    <td><c:out value="${trx.pelanggan.idPelanggan}" /></td>
                    <td><c:out value="${trx.pelanggan.nama}" /></td>
                    <td><fmt:formatNumber value="${trx.biayaService}" type="number" minFractionDigits="0" maxFractionDigits="0"/></td>
                    <td><fmt:formatNumber value="${trx.totalBiaya}" type="number" minFractionDigits="0" maxFractionDigits="0"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/transaksi?action=detail&noTransaksi=${trx.noTransaksi}">Detail</a>
                        |
                        <form action="${pageContext.request.contextPath}/transaksi" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="noTransaksi" value="${trx.noTransaksi}">
                            <button type="submit" onclick="return confirm('Hapus transaksi ${trx.noTransaksi}?');">Hapus</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="7">Tidak ada data transaksi.
                    <c:if test="${not empty startDate && not empty endDate}">
                        Untuk periode <c:out value='${startDate}'/> s/d <c:out value='${endDate}'/>.
                    </c:if>
                </td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App.</p>
</body>
</html>
