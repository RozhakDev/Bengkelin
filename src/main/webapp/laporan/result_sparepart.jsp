<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hasil Laporan Pelanggan per Spare Part - Bengkelin</title>
</head>
<body>
<div>
    <header>
        <h1>Laporan Penggunaan Spare Part</h1>
        <c:if test="${not empty selectedSparePart}">
            <p>Detail untuk: <strong><c:out value="${selectedSparePart.nama}"/> (<c:out value="${selectedSparePart.kodePart}"/>)</strong></p>
        </c:if>
        <a href="${pageContext.request.contextPath}/laporan?action=formSparePart">Pilih Spare Part Lain</a>
    </header>

    <c:if test="${not empty selectedSparePart}">
        <div>
            <div>
                <p>Total Unit Terjual/Digunakan:</p>
                <p>
                    <c:choose>
                        <c:when test="${not empty totalPenggunaanSparePart}">
                            <c:out value="${totalPenggunaanSparePart}"/>
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div>
                <p>Total Pendapatan dari Spare Part Ini:</p>
                <p>Rp
                    <c:choose>
                        <c:when test="${not empty pendapatanDariSparePart}">
                            <fmt:formatNumber value="${pendapatanDariSparePart}" type="number" minFractionDigits="0" maxFractionDigits="0"/>
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <c:if test="${not empty topCustomer}">
                <div>
                    <p>Pelanggan Pengguna Terbanyak:</p>
                    <p><c:out value="${topCustomer.nama}"/></p>
                    <p>(<c:out value="${topCustomer.quantityUsed}"/> unit)</p>
                </div>
            </c:if>
        </div>

        <h2>Daftar Pelanggan Pengguna</h2>
        <table border="1" cellpadding="4" cellspacing="0">
            <thead>
            <tr>
                <th>ID Pelanggan</th>
                <th>Nama Pelanggan</th>
                <th>No. Telepon</th>
                <th>Jumlah Digunakan</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty pelangganListBySparePart}">
                    <c:forEach var="p" items="${pelangganListBySparePart}">
                        <tr>
                            <td><c:out value="${p.idPelanggan}"/></td>
                            <td><c:out value="${p.nama}"/></td>
                            <td><c:out value="${p.noTelepon}" default="-"/></td>
                            <td><c:out value="${p.quantityUsed}"/></td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="4">Tidak ada pelanggan yang menggunakan spare part ini.</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </c:if>

    <c:if test="${empty selectedSparePart && not empty errorMessage}">
        <div>
            <p><strong>Error:</strong></p>
            <p><c:out value="${errorMessage}" /></p>
        </div>
    </c:if>

    <footer>
        <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App. All rights reserved.</p>
    </footer>
</div>
</body>
</html>
